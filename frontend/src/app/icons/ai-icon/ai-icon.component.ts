import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ai-icon',
  standalone: true,
  template: `
    <svg
      [attr.width]="size"
      [attr.height]="size"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <rect x="4" y="4" width="16" height="16" rx="2" stroke="currentColor" stroke-width="2" />
      <circle cx="9" cy="10" r="1.5" fill="currentColor" />
      <circle cx="15" cy="10" r="1.5" fill="currentColor" />
      <path d="M8 15h8" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
    </svg>
  `,
})
export class AiIconComponent {
  @Input() size = 24;
}
