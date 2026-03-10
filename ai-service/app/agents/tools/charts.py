"""
Tool for creating chart specs to be sent to the frontend.
The agent calls this with the chart type, title, labels and datasets; the return value
is the chart spec (later we will send it in the stream so the client can render it).
"""


def create_chart(
    chart_type: str,
    title: str,
    labels: list[str],
    datasets: list[dict],
) -> dict:
    """
    Create a chart specification for the frontend to render.

    Args:
        chart_type: One of 'bar', 'line', 'pie', 'doughnut'.
        title: Chart title shown above the chart.
        labels: List of category labels (e.g. warehouse IDs, product IDs).
        datasets: List of datasets. Each dataset is a dict with keys:
            - "label": string (e.g. "Units")
            - "data": list of numbers (same length as labels).

    Returns:
        ChartSpec for the frontend.
    """
    return {
        "type": chart_type,
        "title": title,
        "labels": labels,
        "datasets": datasets,
    }
