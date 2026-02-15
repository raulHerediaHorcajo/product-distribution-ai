import { Component, Input, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';

import { StockAssignment } from '@models/stock-assignment.model';
import { Store } from '@models/store.model';
import { Warehouse } from '@models/warehouse.model';
import { DashboardFilters } from '../dashboard-filters/dashboard-filters.component';

import * as L from 'leaflet';
import 'leaflet.markercluster';
import 'leaflet-polylinedecorator';

@Component({
  selector: 'app-dashboard-map',
  standalone: true,
  imports: [],
  templateUrl: './dashboard-map.component.html',
  styleUrl: './dashboard-map.component.scss',
})
export class DashboardMapComponent implements AfterViewInit, OnChanges {
  @Input() filters!: DashboardFilters;
  @Input() assignments: StockAssignment[] = [];
  @Input() warehouses: Warehouse[] = [];
  @Input() stores: Store[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private L = (window as any).L as typeof L;

  private map!: L.Map;
  private warehouseMarkers!: L.MarkerClusterGroup;
  private storeMarkers!: L.MarkerClusterGroup;

  ngOnChanges(_changes: SimpleChanges): void {
    if (this.map) {
      this.applyFilters();
    }
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.applyFilters();
  }

  private initMap(): void {
    this.map = this.L.map('map', {
      zoomControl: false,
      attributionControl: false,
      maxBounds: [
        [-90, -180],
        [90, 180],
      ],
      minZoom: 2,
      maxZoom: 19,
      maxBoundsViscosity: 1,
    }).setView([40, 0], 2);

    this.L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://carto.com/">CARTO</a>',
      subdomains: 'abcd',
    }).addTo(this.map);

    window.addEventListener('resize', () => {
      this.map.invalidateSize();
    });

    this.warehouseMarkers = this.L.markerClusterGroup({
      removeOutsideVisibleBounds: false,
      iconCreateFunction: (cluster: L.MarkerCluster) => {
        const childCount = cluster.getChildCount();
        return this.L.divIcon({
          html: `<div><span>${childCount}</span></div>`,
          className: 'marker-cluster marker-cluster-warehouse',
          iconSize: new this.L.Point(40, 40),
        });
      },
      polygonOptions: {
        color: '#111',
      },
    }).addTo(this.map);
    this.storeMarkers = this.L.markerClusterGroup({
      removeOutsideVisibleBounds: false,
      iconCreateFunction: (cluster: L.MarkerCluster) => {
        const childCount = cluster.getChildCount();
        return this.L.divIcon({
          html: `<div><span>${childCount}</span></div>`,
          className: 'marker-cluster marker-cluster-store',
          iconSize: new this.L.Point(40, 40),
        });
      },
      polygonOptions: {
        color: '#666',
      },
    }).addTo(this.map);
  }

  private applyFilters(): void {
    if (!this.map || !this.warehouseMarkers || !this.storeMarkers) return;
    this.warehouseMarkers.clearLayers();
    this.storeMarkers.clearLayers();
    this.map.eachLayer(layer => {
      if (layer instanceof this.L.Polyline || layer instanceof this.L.PolylineDecorator) {
        this.map.removeLayer(layer);
      }
    });

    const filteredAssignments = this.assignments;
    const filteredWarehouses = this.filters.warehouseId
      ? this.warehouses.filter(w => w.id === this.filters.warehouseId)
      : this.warehouses;
    const filteredStores = this.filters.storeId
      ? this.stores.filter(s => s.id === this.filters.storeId)
      : this.stores;
    this.plotRoutes(filteredAssignments, filteredStores, filteredWarehouses);
  }

  private plotRoutes(
    assignments: StockAssignment[],
    stores: Store[],
    warehouses: Warehouse[]
  ): void {
    const storeMap = new Map(stores.map(s => [s.id, s]));
    const warehouseMap = new Map(warehouses.map(w => [w.id, w]));
    stores.forEach(store => {
      const marker = this.L.circleMarker([store.latitude, store.longitude], {
        radius: 6,
        color: '#666',
        fillOpacity: 0.7,
      }).bindTooltip(`Store ${store.id}`);
      this.storeMarkers.addLayer(marker);
    });
    warehouses.forEach(warehouse => {
      const marker = L.circleMarker([warehouse.latitude, warehouse.longitude], {
        radius: 8,
        color: '#111',
        fillOpacity: 1,
      }).bindTooltip(`Warehouse ${warehouse.id}`);
      this.warehouseMarkers.addLayer(marker);
    });

    const routeMap = new Map<string, StockAssignment[]>();
    assignments.forEach(assignment => {
      const key = `${assignment.warehouseId}_${assignment.storeId}`;
      if (!routeMap.has(key)) {
        routeMap.set(key, []);
      }
      routeMap.get(key)!.push(assignment);
    });

    routeMap.forEach(group => {
      const from = warehouseMap.get(group[0].warehouseId);
      const to = storeMap.get(group[0].storeId);
      if (!from || !to) return;
      this.createRouteLineGroup(
        [from.latitude, from.longitude],
        [to.latitude, to.longitude],
        group,
        from.id,
        to.id
      ).addTo(this.map);
    });
  }

  private getPopupContent(group: StockAssignment[], fromId: string, toId: string): string {
    return `
    <div style="max-width: 320px; max-height: 100px; overflow-y: auto;">
      <b>Assignments</b> ${fromId} → ${toId}<br>
      <table style='font-size:0.95em;'>
        <thead style="position: sticky; top: 0; background: white; z-index: 2;"><tr><th>Product</th><th>Size</th><th>Quantity</th></tr></thead>
        <tbody>
          ${group
            .map(
              a => `
            <tr>
              <td>${a.productId}</td>
              <td>${a.size}</td>
              <td>${a.quantity}</td>
            </tr>
          `
            )
            .join('')}
        </tbody>
      </table>
    </div>
    `;
  }

  private createRouteLineGroup(
    from: [number, number],
    to: [number, number],
    group: StockAssignment[],
    fromId: string,
    toId: string
  ): L.Polyline {
    const baseStyle = { color: '#673ab7', weight: 2, opacity: 0.13 };
    const highlightStyle = { color: '#311b92', weight: 4, opacity: 0.8 };
    const polyline = this.L.polyline([from, to], baseStyle).bindPopup(
      this.getPopupContent(group, fromId, toId)
    );

    let decorator = this.createArrowDecoratorGroup(
      polyline,
      group,
      baseStyle.opacity,
      fromId,
      toId
    );
    decorator.addTo(this.map);

    let isPopupOpen = false;
    let isHovering = false;

    const applyHighlight = () => {
      polyline.setStyle(highlightStyle);
      this.map.removeLayer(decorator);
      decorator = this.createArrowDecoratorGroup(
        polyline,
        group,
        highlightStyle.opacity,
        fromId,
        toId
      );
      decorator.addTo(this.map);
    };

    const applyBase = () => {
      polyline.setStyle(baseStyle);
      this.map.removeLayer(decorator);
      decorator = this.createArrowDecoratorGroup(polyline, group, baseStyle.opacity, fromId, toId);
      decorator.addTo(this.map);
    };

    polyline.on('popupopen', () => {
      isPopupOpen = true;
      applyHighlight();
    });

    polyline.on('popupclose', () => {
      isPopupOpen = false;
      if (!isHovering) {
        applyBase();
      }
    });

    polyline.on('mouseover', () => {
      isHovering = true;
      applyHighlight();
    });

    polyline.on('mouseout', () => {
      isHovering = false;
      if (!isPopupOpen) {
        applyBase();
      }
    });

    return polyline;
  }

  private createArrowDecoratorGroup(
    line: L.Polyline,
    group: StockAssignment[],
    opacity: number,
    fromId: string,
    toId: string
  ): L.PolylineDecorator {
    return this.L.polylineDecorator(line, {
      patterns: [
        {
          offset: '50%',
          repeat: 0,
          symbol: this.L.Symbol.arrowHead({
            pixelSize: 12,
            polygon: false,
            pathOptions: { stroke: true, color: '#673ab7', opacity: opacity },
          }),
        },
      ],
    }).bindPopup(this.getPopupContent(group, fromId, toId));
  }
}
