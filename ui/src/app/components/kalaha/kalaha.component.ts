import { Component, OnDestroy } from '@angular/core';
import * as bootstrap from 'bootstrap';
import { Subscription } from 'rxjs';
import { KalahaService } from 'src/app/services/kalaha.service';

@Component({
  selector: 'app-kalaha',
  templateUrl: './kalaha.component.html',
  styleUrls: ['./kalaha.component.scss']
})
export class KalahaComponent implements OnDestroy {
  player1PitNames: string[] = ['A', 'B', 'C', 'D', 'E', 'F'];
  player2PitNames: string[] = ['A', 'B', 'C', 'D', 'E', 'F'].reverse();
  player1Pits: number[] = [6, 6, 6, 6, 6, 6];
  player1BigPit: number = 0;
  player2Pits: number[] = [6, 6, 6, 6, 6, 6];
  player2BigPit: number = 0;
  gameId!: string;
  playerTurn: string = "";
  isGameEnded: boolean = true;
  isPlayer1Turn: boolean = false;
  isPlayer2Turn: boolean = false;
  selectedPit: string = "A";
  loading: boolean = false;
  subscription: Subscription = new Subscription;
  kalahaGame: KalahaGame = new KalahaGame;

  constructor(private kalahaService: KalahaService) { }

  startGame() {
    this.loading = true;
    this.subscription = this.kalahaService.getNewGame().subscribe({
      next: (res: any) => {
        this.kalahaGame = res;
        this.mapResponse();
        this.loading = false;
      },
      error: (e) => console.log(e)
    });
  }

  mapResponse() {
    this.gameId = this.kalahaGame.gameId;
    this.player1Pits = this.kalahaGame.player1Pits;
    this.player1BigPit = this.kalahaGame.player1BigPit;
    this.player2Pits = this.kalahaGame.player2Pits.reverse();
    this.player2BigPit = this.kalahaGame.player2BigPit;
    this.isGameEnded = this.kalahaGame.isGameEnded;
    this.isPlayer1Turn = this.kalahaGame.nextTurn == "PLAYER_1" ? true : false;
    this.isPlayer2Turn = this.kalahaGame.nextTurn == "PLAYER_2" ? true : false;
    this.playerTurn = this.kalahaGame.nextTurn == "PLAYER_1" ? "Player 1 turn" : "Player 2 turn";
  }

  getSelectedPit(pitValue: string) {
    this.selectedPit = pitValue;
  }

  play() {
    this.loading = true;
    this.subscription = this.kalahaService.playGame(this.gameId, this.selectedPit).subscribe({
      next: (res: any) => {
        this.kalahaGame = res;
        this.mapResponse();
        if (this.isGameEnded) {
          this.loading = false;
          const winnerModalElement = document.getElementById('winnerModal');
          if (winnerModalElement) {
            const winnerModal = new bootstrap.Modal(winnerModalElement);
            winnerModal.show();
          }
        }
        this.loading = false;
        this.selectedPit = "A";
      },
      error: (e) => {
        this.loading = false;
        console.log(e);
        alert(e.error.message);
      }
    });
  }

  reset() {
    this.isGameEnded = true;
    this.isPlayer1Turn = false;
    this.isPlayer2Turn = false;
    this.resetPage();
  }

  resetPage(): void {
    location.reload();
  }

  ngOnDestroy(): void {
    this.subscription && this.subscription.unsubscribe();
  }
}

export class KalahaGame {
  gameId!: string;
  player1Pits!: number[];
  player1BigPit!: number;
  player2Pits!: number[];
  player2BigPit!: number;
  isGameEnded!: boolean;
  nextTurn!: string;
  winner!: string;
}
