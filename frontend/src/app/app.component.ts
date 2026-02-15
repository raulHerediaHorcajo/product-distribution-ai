import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { DashboardMetricsComponent } from './dashboard-metrics/dashboard-metrics.component';
import { DashboardMetricsDetailComponent } from './dashboard-metrics-detail/dashboard-metrics-detail.component';
import {
  DashboardFilters,
  DashboardFiltersComponent,
} from './dashboard-filters/dashboard-filters.component';
import { Warehouse } from '@models/warehouse.model';
import { Store } from '@models/store.model';
import { Product } from '@models/product.model';
import { GlobalMetrics, DetailedMetrics } from '@models/metrics.model';
import { DashboardTab } from '@models/dashboard.model';
import { WarehouseService } from '@services/warehouse.service';
import { StoreService } from '@services/store.service';
import { ProductService } from '@services/product.service';
import { MetricsService } from '@services/metrics.service';
import { DashboardMapComponent } from './dashboard-map/dashboard-map.component';
import { DashboardTableComponent } from './dashboard-table/dashboard-table.component';
import { DashboardTabsComponent } from './dashboard-tabs/dashboard-tabs.component';
import { StockAssignment } from '@models/stock-assignment.model';
import { StockAssignmentService } from '@services/stock-assignment.service';
import { forkJoin, Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    DashboardMetricsComponent,
    DashboardMetricsDetailComponent,
    DashboardFiltersComponent,
    DashboardTabsComponent,
    DashboardMapComponent,
    DashboardTableComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy {
  globalMetrics: GlobalMetrics | null = null;
  detailedMetrics: DetailedMetrics | null = null;

  assignments: StockAssignment[] = [];
  warehouses: Warehouse[] = [];
  stores: Store[] = [];
  products: Product[] = [];

  loading = true;
  error: string | null = null;

  filters: DashboardFilters = {
    warehouseId: null,
    storeId: null,
    productId: null,
  };

  activeTab: DashboardTab = 'map';

  private readonly destroy$ = new Subject<void>();

  private readonly stockAssignmentService = inject(StockAssignmentService);
  private readonly warehouseService = inject(WarehouseService);
  private readonly storeService = inject(StoreService);
  private readonly productService = inject(ProductService);
  private readonly metricsService = inject(MetricsService);

  ngOnInit() {
    this.loading = true;
    this.error = null;
    forkJoin({
      warehouses: this.warehouseService.getAll(),
      stores: this.storeService.getAll(),
      products: this.productService.getAll(),
      globalMetrics: this.metricsService.getGlobalMetrics(),
    })
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: ({ warehouses, stores, products, globalMetrics }) => {
          this.warehouses = warehouses;
          this.stores = stores;
          this.products = products;
          this.globalMetrics = globalMetrics;
          this.error = null;
          this.loadAssignments(this.filters);
        },
        error: err => {
          console.error('Error loading data:', err);
          this.error = 'Failed to load data';
        },
      });
  }

  loadAssignments(filters: DashboardFilters) {
    this.loading = true;
    this.error = null;
    this.stockAssignmentService
      .getAll(filters)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: assignments => {
          this.assignments = assignments;
          this.error = null;
        },
        error: err => {
          console.error('Error loading assignments:', err);
          this.error = 'Failed to load assignments';
        },
      });
  }

  onFiltersChange(newFilters: DashboardFilters) {
    this.filters = newFilters;
    this.loadAssignments(this.filters);

    if (this.activeTab === 'metrics') {
      this.loadDetailedMetrics();
    }
  }

  onTabChange(tab: DashboardTab) {
    this.activeTab = tab;

    if (tab === 'metrics') {
      this.loadDetailedMetrics();
    }
  }

  loadDetailedMetrics() {
    this.error = null;

    const criteria = {
      warehouseId: this.filters.warehouseId || undefined,
      storeId: this.filters.storeId || undefined,
      productId: this.filters.productId || undefined,
    };

    this.metricsService
      .getDetailedMetrics(criteria)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: metrics => {
          this.detailedMetrics = metrics;
          this.error = null;
        },
        error: err => {
          console.error('Error loading detailed metrics:', err);
          this.error = 'Failed to load detailed metrics';
        },
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
