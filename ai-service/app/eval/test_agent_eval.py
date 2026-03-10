"""
Pytest integration for ADK eval via AgentEvaluator.evaluate_eval_set.

When any eval case fails, the evaluator asserts and the test fails, so CI fails.
Run from ai-service root so that agent_module="app" resolves (e.g. uv run pytest app/eval/).
"""

import os
from pathlib import Path
import pytest

from google.adk.evaluation.agent_evaluator import AgentEvaluator
from google.adk.evaluation.eval_config import get_evaluation_criteria_or_default

_EVAL_DIR = Path(__file__).resolve().parent

AGENT_MODULE = "app"
EVAL_CONFIG_FILE = "eval_config_ci.json"
EVAL_DATASET_FILE = "sampleDataset.evalset.json"
NUM_RUNS = 1


def _eval_file_path() -> str:
    """Absolute path to the eval dataset (.evalset.json)."""
    return os.path.join(_EVAL_DIR, EVAL_DATASET_FILE)


def _config_path() -> str:
    """Absolute path to the eval config (criteria, judge model, etc.)."""
    return os.path.join(_EVAL_DIR, EVAL_CONFIG_FILE)


@pytest.mark.asyncio
async def test_sample_dataset_eval_passes():
    """Run ADK eval on sampleDataset via evaluate_eval_set; fails if any eval case fails."""
    eval_config = get_evaluation_criteria_or_default(_config_path())
    eval_set = AgentEvaluator._load_eval_set_from_file(
        _eval_file_path(), eval_config, {}
    )

    await AgentEvaluator.evaluate_eval_set(
        agent_module=AGENT_MODULE,
        eval_set=eval_set,
        eval_config=eval_config,
        num_runs=NUM_RUNS,
        print_detailed_results=False,
    )
