import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatToolbarModule, MatButtonModule, MatIconModule],
  template: `
    <mat-toolbar color="primary">
      <span>Knowledge Sharing Platform</span>
      <span class="spacer"></span>
      <button mat-button *ngIf="!authService.isLoggedIn()" routerLink="/login">
        <mat-icon>login</mat-icon>
        Login
      </button>
      <button mat-button *ngIf="authService.isLoggedIn()" (click)="logout()">
        <mat-icon>logout</mat-icon>
        Logout
      </button>
    </mat-toolbar>
    
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .spacer {
      flex: 1 1 auto;
    }
  `]
})
export class AppComponent {
  title = 'knowledge-sharing-frontend';
  
  constructor(public authService: AuthService) {}
  
  logout() {
    this.authService.logout();
  }
}
