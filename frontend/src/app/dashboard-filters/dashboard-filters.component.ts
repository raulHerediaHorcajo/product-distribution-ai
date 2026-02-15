import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Store } from '@models/store.model';
import { Warehouse } from '@models/warehouse.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { Product } from '@models/product.model';

export interface DashboardFilters {
  warehouseId: string | null;
  storeId: string | null;
  productId: string | null;
}

@Component({
  selector: 'app-dashboard-filters',
  standalone: true,
  imports: [CommonModule, FormsModule, NgSelectModule],
  templateUrl: './dashboard-filters.component.html',
  styleUrl: './dashboard-filters.component.scss',
})
export class DashboardFiltersComponent {
  @Input() warehouses: Warehouse[] = [];
  @Input() stores: Store[] = [];
  @Input() products: Product[] = [];
  @Input() filters!: DashboardFilters;

  @Output() filtersChange = new EventEmitter<DashboardFilters>();

  onFiltersChange() {
    this.filtersChange.emit({ ...this.filters });
  }
}
