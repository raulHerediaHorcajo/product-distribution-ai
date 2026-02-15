import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardTab } from '@models/dashboard.model';

@Component({
  selector: 'app-dashboard-tabs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-tabs.component.html',
  styleUrl: './dashboard-tabs.component.scss',
})
export class DashboardTabsComponent {
  @Input() activeTab: DashboardTab = 'map';
  @Output() tabChange = new EventEmitter<DashboardTab>();

  onSetTab(tab: DashboardTab) {
    this.activeTab = tab;
    this.tabChange.emit(tab);
  }
}
