import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { KalahaService } from './kalaha.service';
import { environment } from 'src/environments/environment.development';

describe('KalahaService', () => {
  let service: KalahaService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [KalahaService]
    });
    service = TestBed.inject(KalahaService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve a new game', () => {
    const expectedGame = { gameId: '652b62da2c85ce2218aa6ab7', player1Pits: [6, 6, 6, 6, 6, 6], player1BigPit: 0, player2Pits: [6, 6, 6, 6, 6, 6], player2BigPit: 0, isGameEnded: false, nextTurn: 'PLAYER_1' };

    service.getNewGame().subscribe(game => {
      expect(game).toEqual(expectedGame);
    });

    const req = httpTestingController.expectOne(environment.baseUrl + '/game/new');
    expect(req.request.method).toEqual('GET');
    req.flush(expectedGame);
  });

  it('should play a game', () => {
    const gameId = '652b62da2c85ce2218aa6ab7';
    const selectedPit = 'A';

    service.playGame(gameId, selectedPit).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpTestingController.expectOne(environment.baseUrl + `/game/playGame/${gameId}/playPit/${selectedPit}`);
    expect(req.request.method).toEqual('PATCH');
    req.flush({});
  });
});
