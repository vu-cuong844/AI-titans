import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { DocumentService } from '../../services/document.service';
import { Document } from '../../models/document.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="home-container">
      <div class="welcome-section">
        <h1>Chào mừng đến với Nền tảng Chia sẻ Kiến thức</h1>
        <p>Khám phá, chia sẻ và học hỏi từ cộng đồng nội bộ</p>
        <button mat-raised-button color="primary" routerLink="/upload">
          <mat-icon>upload</mat-icon>
          Tải lên tài liệu mới
        </button>
      </div>

      <div class="content-grid">
        <!-- Top 5 tài liệu mới nhất -->
        <mat-card class="content-card">
          <mat-card-header>
            <mat-card-title>Tài liệu mới nhất</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div *ngIf="loadingRecent" class="loading">
              <mat-spinner diameter="30"></mat-spinner>
            </div>
            <div *ngIf="!loadingRecent && recentDocuments.length === 0" class="no-data">
              Chưa có tài liệu nào
            </div>
            <div *ngIf="!loadingRecent && recentDocuments.length > 0">
              <div *ngFor="let doc of recentDocuments" class="document-item" 
                   (click)="viewDocument(doc.id)">
                <h4>{{ doc.title }}</h4>
                <p class="summary">{{ doc.summary }}</p>
                <div class="document-meta">
                  <span class="author">{{ doc.createdByName }}</span>
                  <span class="date">{{ formatDate(doc.createdAt) }}</span>
                  <span class="sharing-level" [ngClass]="doc.sharingLevel.toLowerCase()">
                    {{ getSharingLevelText(doc.sharingLevel) }}
                  </span>
                </div>
                <div *ngIf="doc.tags && doc.tags.length > 0" class="tags">
                  <mat-chip *ngFor="let tag of doc.tags" class="tag">{{ tag }}</mat-chip>
                </div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- Top 5 tài liệu nổi bật -->
        <mat-card class="content-card">
          <mat-card-header>
            <mat-card-title>Tài liệu nổi bật</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div *ngIf="loadingStarred" class="loading">
              <mat-spinner diameter="30"></mat-spinner>
            </div>
            <div *ngIf="!loadingStarred && starredDocuments.length === 0" class="no-data">
              Chưa có tài liệu nào
            </div>
            <div *ngIf="!loadingStarred && starredDocuments.length > 0">
              <div *ngFor="let doc of starredDocuments" class="document-item" 
                   (click)="viewDocument(doc.id)">
                <h4>{{ doc.title }}</h4>
                <p class="summary">{{ doc.summary }}</p>
                <div class="document-meta">
                  <span class="author">{{ doc.createdByName }}</span>
                  <span class="stars">
                    <mat-icon>star</mat-icon>
                    {{ doc.starCount }}
                  </span>
                  <span class="sharing-level" [ngClass]="doc.sharingLevel.toLowerCase()">
                    {{ getSharingLevelText(doc.sharingLevel) }}
                  </span>
                </div>
                <div *ngIf="doc.tags && doc.tags.length > 0" class="tags">
                  <mat-chip *ngFor="let tag of doc.tags" class="tag">{{ tag }}</mat-chip>
                </div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- Tài liệu của tôi -->
        <mat-card class="content-card">
          <mat-card-header>
            <mat-card-title>Tài liệu của tôi</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div *ngIf="loadingMyDocs" class="loading">
              <mat-spinner diameter="30"></mat-spinner>
            </div>
            <div *ngIf="!loadingMyDocs && myDocuments.length === 0" class="no-data">
              Bạn chưa tạo tài liệu nào
            </div>
            <div *ngIf="!loadingMyDocs && myDocuments.length > 0">
              <div *ngFor="let doc of myDocuments" class="document-item" 
                   (click)="viewDocument(doc.id)">
                <h4>{{ doc.title }}</h4>
                <p class="summary">{{ doc.summary }}</p>
                <div class="document-meta">
                  <span class="date">{{ formatDate(doc.createdAt) }}</span>
                  <span class="views">
                    <mat-icon>visibility</mat-icon>
                    {{ doc.viewCount }}
                  </span>
                  <span class="sharing-level" [ngClass]="doc.sharingLevel.toLowerCase()">
                    {{ getSharingLevelText(doc.sharingLevel) }}
                  </span>
                </div>
                <div *ngIf="doc.tags && doc.tags.length > 0" class="tags">
                  <mat-chip *ngFor="let tag of doc.tags" class="tag">{{ tag }}</mat-chip>
                </div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>
      </div>

      <div class="actions">
        <button mat-raised-button color="accent" routerLink="/documents">
          <mat-icon>list</mat-icon>
          Xem tất cả tài liệu
        </button>
      </div>
    </div>
  `,
  styles: [`
    .home-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .welcome-section {
      text-align: center;
      margin-bottom: 40px;
      padding: 40px 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 12px;
    }
    
    .welcome-section h1 {
      font-size: 2.5rem;
      margin-bottom: 10px;
    }
    
    .welcome-section p {
      font-size: 1.2rem;
      margin-bottom: 30px;
      opacity: 0.9;
    }
    
    .content-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }
    
    .content-card {
      height: fit-content;
    }
    
    .document-item {
      padding: 15px;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      margin-bottom: 10px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .document-item:hover {
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
      transform: translateY(-2px);
    }
    
    .document-item h4 {
      margin: 0 0 8px 0;
      color: #1976d2;
      font-size: 1.1rem;
    }
    
    .summary {
      color: #666;
      font-size: 0.9rem;
      margin: 0 0 10px 0;
      line-height: 1.4;
    }
    
    .document-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 0.8rem;
      color: #888;
      margin-bottom: 8px;
    }
    
    .tags {
      margin-top: 8px;
    }
    
    .loading, .no-data {
      text-align: center;
      padding: 20px;
      color: #666;
    }
    
    .actions {
      text-align: center;
      margin-top: 30px;
    }
  `]
})
export class HomeComponent implements OnInit {
  recentDocuments: Document[] = [];
  starredDocuments: Document[] = [];
  myDocuments: Document[] = [];
  
  loadingRecent = false;
  loadingStarred = false;
  loadingMyDocs = false;

  constructor(
    private documentService: DocumentService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadRecentDocuments();
    this.loadStarredDocuments();
    this.loadMyDocuments();
  }

  loadRecentDocuments() {
    this.loadingRecent = true;
    this.documentService.getRecentDocuments(5).subscribe({
      next: (docs) => {
        this.recentDocuments = docs;
        this.loadingRecent = false;
      },
      error: (error) => {
        console.error('Error loading recent documents:', error);
        this.loadingRecent = false;
      }
    });
  }

  loadStarredDocuments() {
    this.loadingStarred = true;
    this.documentService.getTopStarredDocuments(5).subscribe({
      next: (docs) => {
        this.starredDocuments = docs;
        this.loadingStarred = false;
      },
      error: (error) => {
        console.error('Error loading starred documents:', error);
        this.loadingStarred = false;
      }
    });
  }

  loadMyDocuments() {
    this.loadingMyDocs = true;
    this.documentService.getMyDocuments(5).subscribe({
      next: (docs) => {
        this.myDocuments = docs;
        this.loadingMyDocs = false;
      },
      error: (error) => {
        console.error('Error loading my documents:', error);
        this.loadingMyDocs = false;
      }
    });
  }

  viewDocument(id: number) {
    this.router.navigate(['/documents', id]);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
  }

  getSharingLevelText(level: string): string {
    switch (level) {
      case 'PRIVATE': return 'Riêng tư';
      case 'GROUP': return 'Nhóm';
      case 'PUBLIC': return 'Công khai';
      default: return level;
    }
  }
}
