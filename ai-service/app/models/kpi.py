"""KPI domain (names, descriptions) and Pydantic models for KPI explanation API request/response."""

from pydantic import BaseModel

KPI_NAMES: dict[str, str] = {
    "efficiency-score": "Efficiency Score",
    "fulfillment-rate": "Fulfillment Rate",
    "total-distance": "Total Distance",
    "stores-served": "Stores Served",
    "avg-shipment-size": "Avg Shipment Size",
    "unique-products": "Unique Products",
    "unfulfilled-demand": "Unfulfilled Demand",
    "fulfilled-demand": "Fulfilled Demand",
    "capacity-utilization": "Capacity Utilization",
    "distance-distribution": "Distance Distribution",
    "units-by-warehouse": "Units by Warehouse",
    "top-products": "Top 5 Products",
}

# Short domain descriptions to ground the model.
KPI_DESCRIPTIONS: dict[str, str] = {
    "efficiency-score": (
        "a composite score from 0 to 100 that summarizes how well distribution "
        "operations meet demand, combining fulfillment performance, distance and "
        "capacity utilization."
    ),
    "fulfillment-rate": ("the percentage of requested units that were actually delivered to stores within the period."),
    "total-distance": ("the total kilometers traveled by all deliveries from warehouses to stores in the period."),
    "stores-served": (
        "how many stores received at least one delivery, how many were fully served "
        "according to their demand, and how many were never served."
    ),
    "avg-shipment-size": ("the average number of units per shipment sent from warehouses to stores."),
    "unique-products": (
        "how many distinct products were requested by stores and how many of those were actually distributed."
    ),
    "unfulfilled-demand": (
        "the total units requested by stores that were not delivered, broken down "
        "by lack of stock and lack of store capacity."
    ),
    "fulfilled-demand": ("the total units requested by stores that were successfully delivered."),
    "capacity-utilization": (
        "how much of the available store capacity is being used, and how many stores are already at their capacity limit."
    ),
    "distance-distribution": (
        "the spread of delivery distances, typically looking at minimum, median "
        "and maximum kilometers between warehouses and stores."
    ),
    "units-by-warehouse": (
        "how many units each warehouse shipped, used to compare workload and "
        "throughput across warehouses. It shows whether load is well balanced "
        "between warehouses, or if some are barely used while others carry most "
        "of the work. Large gaps between warehouses help you spot special cases: "
        "underused capacity, bottlenecks, or star performers."
    ),
    "top-products": (
        "the products with the highest shipped volume in the period, ranked by "
        "total delivered units. This helps you see which products are most important "
        "for your distribution network and where you should focus attention. If there "
        "is a large gap between items in the top 5, it usually indicates one or two "
        "clear star products."
    ),
}


class ExplainKpiRequest(BaseModel):
    kpi_slug: str
    context: dict


class ExplainKpiResponse(BaseModel):
    explanation: str
