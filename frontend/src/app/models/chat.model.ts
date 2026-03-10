export interface ChartSpec {
  type: 'bar' | 'line' | 'pie' | 'doughnut';
  title?: string;
  labels: string[];
  datasets: { label: string; data: number[] }[];
}

export interface ChatChartData {
  labels: string[];
  datasets: { label: string; data: number[]; backgroundColor?: string[] }[];
}

export interface ChatChartOptions {
  responsive: boolean;
  maintainAspectRatio: boolean;
  plugins: {
    legend: { display: boolean; position?: 'top' };
  };
  scales?: {
    x?: { display?: boolean; beginAtZero?: boolean };
    y?: { display?: boolean; beginAtZero?: boolean };
  };
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  text: string;
  charts?: ChartSpec[];
}

export interface ChatResponse {
  reply: string;
}

export interface ChatStreamChunk {
  delta?: string;
  done?: boolean;
  error?: string;
  chart?: ChartSpec;
}

export const SUGGESTED_PROMPTS: string[] = [
  'What is the fulfillment rate?',
  'What is the most distributed size?',
  'Which product has had the most unfulfilled demand?',
];
