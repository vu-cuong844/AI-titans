import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DocumentService } from '../../services/document.service';
import { Document } from '../../models/document.model';

@Component({
  selector: 'app-document-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="document-detail-container">
      <div *ngIf="loading" class="loading">
        <mat-spinner></mat-spinner>
        <p>Đang tải tài liệu...</p>
      </div>

      <div *ngIf="!loading && !document" class="error">
        <mat-icon>error</mat-icon>
        <h3>Tài liệu không tồn tại</h3>
        <p>Không thể tìm thấy tài liệu hoặc bạn không có quyền truy cập</p>
        <button mat-raised-button color="primary" (click)="goBack()">
          Quay lại
        </button>
      </div>

      <div *ngIf="!loading && document" class="document-content">
        <!-- Header -->
        <div class="document-header">
          <div class="title-section">
            <h1>{{ document.title }}</h1>
            <div class="document-meta">
              <span class="author">
                <mat-icon>person</mat-icon>
                {{ document.createdByName }}
              </span>
              <span class="date">
                <mat-icon>schedule</mat-icon>
                {{ formatDate(document.createdAt) }}
              </span>
              <span class="sharing-level" [ngClass]="document.sharingLevel.toLowerCase()">
                {{ getSharingLevelText(document.sharingLevel) }}
              </span>
            </div>
          </div>
          
          <div class="actions">
            <button mat-raised-button color="primary" (click)="starDocument()">
              <mat-icon>star</mat-icon>
              Đánh dấu sao ({{ document.starCount }})
            </button>
            <button mat-button (click)="goBack()">
              <mat-icon>arrow_back</mat-icon>
              Quay lại
            </button>
          </div>
        </div>

        <!-- Document Info -->
        <mat-card class="info-card">
          <mat-card-header>
            <mat-card-title>Thông tin tài liệu</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="info-grid">
              <div class="info-item">
                <label>Tên file gốc:</label>
                <span>{{ document.originalFileName }}</span>
              </div>
              <div class="info-item">
                <label>Loại file:</label>
                <span>{{ document.fileType }}</span>
              </div>
              <div class="info-item">
                <label>Kích thước:</label>
                <span>{{ formatFileSize(document.fileSize) }}</span>
              </div>
              <div class="info-item">
                <label>Phòng ban:</label>
                <span>{{ document.department }}</span>
              </div>
              <div class="info-item">
                <label>Lượt xem:</label>
                <span>{{ document.viewCount }}</span>
              </div>
              <div class="info-item">
                <label>Lượt đánh dấu sao:</label>
                <span>{{ document.starCount }}</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- Summary -->
        <mat-card *ngIf="document.summary" class="summary-card">
          <mat-card-header>
            <mat-card-title>Tóm tắt</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p>{{ document.summary }}</p>
          </mat-card-content>
        </mat-card>

        <!-- Tags -->
        <mat-card *ngIf="document.tags && document.tags.length > 0" class="tags-card">
          <mat-card-header>
            <mat-card-title>Tags</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="tags">
              <mat-chip *ngFor="let tag of document.tags" class="tag">{{ tag }}</mat-chip>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- Content -->
        <mat-card *ngIf="document.content" class="content-card">
          <mat-card-header>
            <mat-card-title>Nội dung</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="content" [innerHTML]="formatContent(document.content)"></div>
          </mat-card-content>
        </mat-card>

        <!-- File Download -->
        <mat-card class="download-card">
          <mat-card-header>
            <mat-card-title>Tải xuống</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p>Bạn có thể tải xuống file gốc của tài liệu này</p>
            <button mat-raised-button color="accent" (click)="downloadFile()">
              <mat-icon>download</mat-icon>
              Tải xuống {{ document.originalFileName }}
            </button>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .document-detail-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .loading, .error {
      text-align: center;
      padding: 40px;
      color: #666;
    }
    
    .error mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
      margin-bottom: 16px;
      color: #f44336;
    }
    
    .document-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 30px;
      padding: 20px;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      border-radius: 12px;
    }
    
    .title-section h1 {
      margin: 0 0 15px 0;
      color: #1976d2;
      font-size: 2rem;
    }
    
    .document-meta {
      display: flex;
      gap: 20px;
      align-items: center;
      flex-wrap: wrap;
    }
    
    .document-meta span {
      display: flex;
      align-items: center;
      gap: 5px;
      color: #666;
    }
    
    .document-meta mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }
    
    .actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }
    
    .info-card, .summary-card, .tags-card, .content-card, .download-card {
      margin-bottom: 20px;
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 15px;
    }
    
    .info-item {
      display: flex;
      flex-direction: column;
      gap: 5px;
    }
    
    .info-item label {
      font-weight: 500;
      color: #666;
      font-size: 0.9rem;
    }
    
    .info-item span {
      color: #333;
    }
    
    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
    
    .content {
      line-height: 1.6;
      color: #333;
    }
    
    .content pre {
      background: #f5f5f5;
      padding: 15px;
      border-radius: 4px;
      overflow-x: auto;
    }
    
    .content code {
      background: #f5f5f5;
      padding: 2px 4px;
      border-radius: 3px;
      font-family: 'Courier New', monospace;
    }
  `]
})
export class DocumentDetailComponent implements OnInit {
  document: Document | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private documentService: DocumentService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadDocument(+id);
    }
  }

  loadDocument(id: number) {
    this.loading = true;
    this.documentService.getDocument(id).subscribe({
      next: (document) => {
        this.document = document;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading document:', error);
        this.loading = false;
        this.document = null;
      }
    });
  }

  starDocument() {
    if (this.document) {
      this.documentService.starDocument(this.document.id).subscribe({
        next: (response) => {
          this.snackBar.open('Đã đánh dấu sao tài liệu', 'Đóng', {
            duration: 3000
          });
          // Reload document to update star count
          this.loadDocument(this.document!.id);
        },
        error: (error) => {
          console.error('Error starring document:', error);
          this.snackBar.open('Lỗi khi đánh dấu sao tài liệu', 'Đóng', {
            duration: 3000
          });
        }
      });
    }
  }

  downloadFile() {
    if (this.document) {
      // In a real implementation, this would download the actual file
      this.snackBar.open('Tính năng tải xuống sẽ được triển khai', 'Đóng', {
        duration: 3000
      });
    }
  }

  goBack() {
    this.router.navigate(['/documents']);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  getSharingLevelText(level: string): string {
    switch (level) {
      case 'PRIVATE': return 'Riêng tư';
      case 'GROUP': return 'Nhóm';
      case 'PUBLIC': return 'Công khai';
      default: return level;
    }
  }

  formatContent(content: string): string {
    // Basic content formatting - in a real app, you might use a markdown parser
    return content
      .replace(/\n/g, '<br>')
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>');
  }
}
