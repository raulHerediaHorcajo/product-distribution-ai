import {
  Component,
  OnInit,
  DestroyRef,
  ElementRef,
  ViewChild,
  AfterViewChecked,
  inject,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '@services/chat.service';
import { ChatMessage, SUGGESTED_PROMPTS } from '@models/chat.model';
import { AiIconComponent } from '@icons/ai-icon/ai-icon.component';
import { SendIconComponent } from '@icons/send-icon/send-icon.component';
import { MarkdownComponent } from 'ngx-markdown';
import { ChatChartComponent } from './chat-chart/chat-chart.component';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AiIconComponent,
    SendIconComponent,
    MarkdownComponent,
    ChatChartComponent,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
})
export class ChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('messagesEnd') private messagesEndRef!: ElementRef<HTMLDivElement>;

  messages: ChatMessage[] = [];
  sessionId = '';
  loading = false;
  error: string | null = null;
  inputText = '';
  readonly suggestedPrompts = SUGGESTED_PROMPTS;

  private readonly chatService = inject(ChatService);
  private readonly destroyRef = inject(DestroyRef);
  private shouldScrollToEnd = false;

  ngOnInit(): void {
    this.sessionId = crypto.randomUUID();
  }

  ngAfterViewChecked(): void {
    if (this.shouldScrollToEnd && this.messagesEndRef) {
      this.messagesEndRef.nativeElement.scrollIntoView({ behavior: 'smooth' });
      this.shouldScrollToEnd = false;
    }
  }

  startNewChat(): void {
    this.messages = [];
    this.sessionId = crypto.randomUUID();
    this.error = null;
  }

  sendMessage(text?: string): void {
    const content = (text ?? this.inputText?.trim()) || '';
    if (!content || this.loading) return;

    this.inputText = '';
    this.error = null;
    this.messages.push({ role: 'user', text: content });
    this.shouldScrollToEnd = true;
    this.loading = true;

    let assistantMessage: ChatMessage | null = null;
    this.chatService
      .sendMessageStreaming(content, this.sessionId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: data => {
        if (data.chart) {
          if (!assistantMessage) {
            assistantMessage = { role: 'assistant', text: '', charts: [data.chart] };
            this.messages.push(assistantMessage);
            this.loading = false;
          } else {
            if (!assistantMessage.charts) assistantMessage.charts = [];
            assistantMessage.charts.push(data.chart);
          }
          this.shouldScrollToEnd = true;
        }
        if (data.delta !== undefined) {
          if (!assistantMessage) {
            assistantMessage = { role: 'assistant', text: data.delta };
            this.messages.push(assistantMessage);
            this.loading = false;
          } else {
            assistantMessage.text += data.delta;
          }
          this.shouldScrollToEnd = true;
        }
        if (data.done) {
          this.loading = false;
          this.shouldScrollToEnd = true;
        }
      },
      complete: () => {
        this.loading = false;
        if (!assistantMessage) {
          this.messages.push({
            role: 'assistant',
            text: "I didn't get a reply from the agent.",
          });
        } else if (!assistantMessage.text.trim()) {
          assistantMessage.text = "I didn't get a reply from the agent.";
        }
        this.shouldScrollToEnd = true;
      },
      error: err => {
        this.loading = false;
        this.error = err?.message || 'Failed to get a response. Please try again.';
        this.shouldScrollToEnd = true;
      },
    });
  }

  onSuggestionClick(prompt: string): void {
    this.sendMessage(prompt);
  }
}
