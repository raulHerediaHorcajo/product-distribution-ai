export interface GlobalMetrics {
  totalShipments: number;
  fulfilledUnits: number;
  unfulfilledUnits: number;
  averageDistance: number;
}

export interface DetailedMetrics {
  efficiencyScore: number;
  fulfillmentRate: number;
  totalDistance: number;
  totalShipments: number;
  storesServed: StoresServed;
  avgShipmentSize: number;
  uniqueProductsDistributed: number;
  uniqueProductsRequested: number;
  capacityUtilization: CapacityUtilization;
  unfulfilledDemand: UnfulfilledDemand;
  fulfilledUnits: number;
  distanceStats: DistanceStats;
  unitsByWarehouse: WarehouseUnits[];
  topProducts: TopProduct[];
}

export interface StoresServed {
  servedStores: number;
  fullyServedStores: number;
  neverServedStores: number;
  coveragePercentage: number;
  fullyServedPercentage: number;
}

export interface CapacityUtilization {
  percentage: number;
  storesAtCapacity: number;
  totalStores: number;
}

export interface UnfulfilledDemand {
  totalUnits: number;
  unitsByStockShortage: number;
  unitsByCapacityShortage: number;
}

export interface DistanceStats {
  minDistance: number;
  maxDistance: number;
  medianDistance: number;
}

export interface WarehouseUnits {
  warehouseId: string;
  totalUnits: number;
  percentage: number;
  avgDistance: number;
}

export interface TopProduct {
  productId: string;
  totalQuantity: number;
}

export interface MetricsCriteria {
  warehouseId?: string;
  storeId?: string;
  productId?: string;
}

export interface ChartData {
  labels: string[];
  datasets: ChartDataset[];
}

export interface ChartDataset {
  label: string;
  data: number[];
  backgroundColor?: string | string[];
  borderColor?: string | string[];
  borderWidth?: number;
}

export interface ChartOptions {
  responsive: boolean;
  maintainAspectRatio: boolean;
  plugins?: {
    legend?: {
      display: boolean;
      position?: 'top' | 'bottom' | 'left' | 'right';
    };
    tooltip?: {
      enabled: boolean;
      callbacks?: {
        label?: (context: { dataIndex: number; label?: string }) => string;
      };
    };
  };
  scales?: {
    x?: {
      beginAtZero?: boolean;
      title?: {
        display: boolean;
        text: string;
      };
    };
    y?: {
      beginAtZero?: boolean;
      title?: {
        display: boolean;
        text: string;
      };
    };
  };
}

export const METRICS_COLORS = {
  primary: '#3B82F6',
  secondary: '#10B981',
  accent: '#F59E0B',
  danger: '#EF4444',
  neutral: '#6B7280',
  success: '#059669',
  warning: '#D97706',
  info: '#0EA5E9',
} as const;

export const CHART_COLORS = [
  '#3B82F6',
  '#10B981',
  '#F59E0B',
  '#EF4444',
  '#8B5CF6',
  '#06B6D4',
  '#84CC16',
  '#F97316',
] as const;
