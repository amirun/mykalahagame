import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KalahaService {

  constructor(private http: HttpClient) { }

  getNewGame() {
    return this.http.get(environment.baseUrl + '/game/new');
  }

  playGame(gameId: string, selectedPit: string) {
    return this.http.patch(environment.baseUrl + '/game/playGame/' + gameId + '/playPit/' + selectedPit, {});
  }
}
