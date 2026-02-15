import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { ScrollingModule, CdkVirtualScrollViewport } from '@angular/cdk/scrolling';
import { CommonModule } from '@angular/common';
import { StockAssignment } from '@models/stock-assignment.model';
import { Warehouse } from '@models/warehouse.model';
import { Store } from '@models/store.model';
import { FormatNumberPipe } from '@pipes/format-number.pipe';
import { FormatDistancePipe } from '@pipes/format-distance.pipe';

@Component({
  selector: 'app-dashboard-table',
  standalone: true,
  imports: [CommonModule, ScrollingModule, FormatNumberPipe, FormatDistancePipe],
  templateUrl: './dashboard-table.component.html',
  styleUrl: './dashboard-table.component.scss',
})
export class DashboardTableComponent implements OnChanges {
  @ViewChild(CdkVirtualScrollViewport) viewport!: CdkVirtualScrollViewport;
  @Input() assignments: StockAssignment[] = [];
  @Input() warehouses: Warehouse[] = [];
  @Input() stores: Store[] = [];

  warehouseCountryMap = new Map<string, string>();
  storeCountryMap = new Map<string, string>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['assignments']) {
      this.warehouseCountryMap = new Map(this.warehouses.map(w => [w.id, w.country]));
      this.storeCountryMap = new Map(this.stores.map(s => [s.id, s.country]));
      setTimeout(() => {
        this.viewport.checkViewportSize();
        this.viewport.scrollToOffset(0, 'smooth');
      });
    }
  }

  getWarehouseCountry(id: string): string {
    return this.warehouseCountryMap.get(id) || '';
  }

  getStoreCountry(id: string): string {
    return this.storeCountryMap.get(id) || '';
  }

  trackByAssignmentId(index: number, assignment: StockAssignment): string {
    return assignment.id;
  }
}
