"""
Pydantic models for metrics API responses.
Matches backend DTOs (camelCase in JSON).
"""

from pydantic import BaseModel, Field


class GlobalMetrics(BaseModel):
    """Global distribution metrics; matches backend GlobalMetricsDTO (camelCase in JSON)."""

    total_shipments: int = Field(alias="totalShipments")
    fulfilled_units: int = Field(alias="fulfilledUnits")
    unfulfilled_units: int = Field(alias="unfulfilledUnits")
    average_distance: float = Field(alias="averageDistance")

    model_config = {"populate_by_name": True}


class StoresServed(BaseModel):
    served_stores: int = Field(alias="servedStores")
    fully_served_stores: int = Field(alias="fullyServedStores")
    never_served_stores: int = Field(alias="neverServedStores")
    coverage_percentage: float = Field(alias="coveragePercentage")
    fully_served_percentage: float = Field(alias="fullyServedPercentage")

    model_config = {"populate_by_name": True}


class CapacityUtilization(BaseModel):
    percentage: float = Field(alias="percentage")
    stores_at_capacity: int = Field(alias="storesAtCapacity")
    total_stores: int = Field(alias="totalStores")

    model_config = {"populate_by_name": True}


class UnfulfilledDemand(BaseModel):
    total_units: int = Field(alias="totalUnits")
    units_by_stock_shortage: int = Field(alias="unitsByStockShortage")
    units_by_capacity_shortage: int = Field(alias="unitsByCapacityShortage")

    model_config = {"populate_by_name": True}


class DistanceStats(BaseModel):
    min_distance: float = Field(alias="minDistance")
    max_distance: float = Field(alias="maxDistance")
    median_distance: float = Field(alias="medianDistance")

    model_config = {"populate_by_name": True}


class WarehouseUnits(BaseModel):
    warehouse_id: str = Field(alias="warehouseId")
    total_units: int = Field(alias="totalUnits")
    percentage: float = Field(alias="percentage")
    avg_distance: float = Field(alias="avgDistance")

    model_config = {"populate_by_name": True}


class TopProduct(BaseModel):
    product_id: str = Field(alias="productId")
    total_quantity: int = Field(alias="totalQuantity")

    model_config = {"populate_by_name": True}


class DetailedMetrics(BaseModel):
    """Detailed distribution metrics; matches backend DetailedMetricsDTO."""

    efficiency_score: int = Field(alias="efficiencyScore")
    fulfillment_rate: float = Field(alias="fulfillmentRate")
    total_distance: float = Field(alias="totalDistance")
    total_shipments: int = Field(alias="totalShipments")
    stores_served: StoresServed = Field(alias="storesServed")
    avg_shipment_size: float = Field(alias="avgShipmentSize")
    unique_products_distributed: int = Field(alias="uniqueProductsDistributed")
    unique_products_requested: int = Field(alias="uniqueProductsRequested")
    capacity_utilization: CapacityUtilization = Field(alias="capacityUtilization")
    unfulfilled_demand: UnfulfilledDemand = Field(alias="unfulfilledDemand")
    fulfilled_units: int = Field(alias="fulfilledUnits")
    distance_stats: DistanceStats = Field(alias="distanceStats")
    units_by_warehouse: list[WarehouseUnits] = Field(alias="unitsByWarehouse", default_factory=list)
    top_products: list[TopProduct] = Field(alias="topProducts", default_factory=list)

    model_config = {"populate_by_name": True}
