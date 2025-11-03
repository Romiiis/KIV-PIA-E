import {Component, inject, OnInit, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {AuthService} from '@core/auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected readonly title = signal('Client');
  private auth = inject(AuthService);


  async ngOnInit() {
    console.log('App initialized');
    await this.auth.initialize();

  }

}
