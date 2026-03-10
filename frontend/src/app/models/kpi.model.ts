import type { DetailedMetrics } from '@models/metrics.model';

export type KpiSlug =
  | 'efficiency-score'
  | 'fulfillment-rate'
  | 'total-distance'
  | 'stores-served'
  | 'avg-shipment-size'
  | 'unique-products'
  | 'unfulfilled-demand'
  | 'fulfilled-demand'
  | 'capacity-utilization'
  | 'distance-distribution'
  | 'units-by-warehouse'
  | 'top-products';

export interface KpiDefinition {
  label: string;
  buildContext: (m: DetailedMetrics) => Record<string, unknown>;
}

export interface KpiExplainResponse {
  explanation: string;
}

export const KPI_CONFIG: Record<KpiSlug, KpiDefinition> = {
  'efficiency-score': {
    label: 'Efficiency Score',
    buildContext: m => ({
      efficiencyScore: m.efficiencyScore,
      fulfillmentRate: m.fulfillmentRate,
      totalDistance: m.totalDistance,
      capacityUtilization: m.capacityUtilization,
    }),
  },
  'fulfillment-rate': {
    label: 'Fulfillment Rate',
    buildContext: m => ({
      fulfillmentRate: m.fulfillmentRate,
      fulfilledUnits: m.fulfilledUnits,
      unfulfilledUnits: m.unfulfilledDemand.totalUnits,
      totalShipments: m.totalShipments,
    }),
  },
  'total-distance': {
    label: 'Total Distance',
    buildContext: m => ({
      totalDistance: m.totalDistance,
      totalShipments: m.totalShipments,
    }),
  },
  'stores-served': {
    label: 'Stores Served',
    buildContext: m => ({
      storesServed: m.storesServed,
    }),
  },
  'avg-shipment-size': {
    label: 'Avg Shipment Size',
    buildContext: m => ({
      avgShipmentSize: m.avgShipmentSize,
      totalShipments: m.totalShipments,
    }),
  },
  'unique-products': {
    label: 'Unique Products',
    buildContext: m => ({
      uniqueProductsDistributed: m.uniqueProductsDistributed,
      uniqueProductsRequested: m.uniqueProductsRequested,
    }),
  },
  'unfulfilled-demand': {
    label: 'Unfulfilled Demand',
    buildContext: m => ({
      unfulfilledDemand: m.unfulfilledDemand,
      fulfilledUnits: m.fulfilledUnits,
    }),
  },
  'fulfilled-demand': {
    label: 'Fulfilled Demand',
    buildContext: m => ({
      fulfilledUnits: m.fulfilledUnits,
      unfulfilledUnits: m.unfulfilledDemand.totalUnits,
    }),
  },
  'capacity-utilization': {
    label: 'Capacity Utilization',
    buildContext: m => ({
      capacityUtilization: m.capacityUtilization,
    }),
  },
  'distance-distribution': {
    label: 'Distance Distribution',
    buildContext: m => ({
      distanceStats: m.distanceStats,
    }),
  },
  'units-by-warehouse': {
    label: 'Units by Warehouse',
    buildContext: m => ({
      unitsByWarehouse: m.unitsByWarehouse,
      fulfilledUnits: m.fulfilledUnits,
      unfulfilledUnits: m.unfulfilledDemand.totalUnits,
    }),
  },
  'top-products': {
    label: 'Top 5 Products',
    buildContext: m => ({
      topProducts: m.topProducts,
      fulfilledUnits: m.fulfilledUnits,
      uniqueProductsDistributed: m.uniqueProductsDistributed,
    }),
  },
};
