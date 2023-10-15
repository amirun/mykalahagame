import { ComponentFixture, TestBed, async, fakeAsync, tick } from '@angular/core/testing';
import { KalahaComponent } from './kalaha.component';
import { KalahaService } from 'src/app/services/kalaha.service';
import { of } from 'rxjs';
import { HttpClientModule } from '@angular/common/http';

describe('KalahaComponent', () => {
  let component: KalahaComponent;
  let fixture: ComponentFixture<KalahaComponent>;
  let kalahaService: KalahaService;
  const mockKalahaGame = {
    gameId: '652b02da2c85ce2218aa6ab7',
    player1Pits: [6, 0, 7, 7, 7, 7],
    player1BigPit: 1,
    player2Pits: [7, 6, 6, 6, 6, 6],
    player2BigPit: 0,
    isGameEnded: false,
    nextTurn: 'PLAYER_2',
    winner: '',
  };
  const mockWinnerKalahaGame = {
    gameId: '652c14366626b37cf411e19b',
    player1Pits: [0, 0, 0, 0, 0, 0],
    player1BigPit: 40,
    player2Pits: [1, 1, 5, 1, 1, 1],
    player2BigPit: 22,
    isGameEnded: true,
    nextTurn: 'PLAYER_1',
    winner: 'PLAYER_1',
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [KalahaComponent],
      imports: [
        HttpClientModule
      ],
      providers: [KalahaService],
    }).compileComponents();

    fixture = TestBed.createComponent(KalahaComponent);
    component = fixture.componentInstance;
    kalahaService = TestBed.inject(KalahaService);
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start the game', fakeAsync(() => {
    spyOn(kalahaService, 'getNewGame').and.returnValue(of(mockKalahaGame));

    component.startGame();
    tick();

    expect(component.kalahaGame).toEqual(mockKalahaGame);
  }));

  it('should play the game', fakeAsync(() => {
    spyOn(kalahaService, 'playGame').and.returnValue(of(mockKalahaGame));

    component.play();
    tick();

    expect(component.kalahaGame).toEqual(mockKalahaGame);
  }));

  it('should have the winner',() => {
    spyOn(kalahaService, 'playGame').and.returnValue(of(mockWinnerKalahaGame));
    component.play();
    expect(component.kalahaGame).toEqual(mockWinnerKalahaGame);
  });

  it('should reset the game', () => {
    const reloadSpy = spyOn(component, 'resetPage');
    component.reset();
    expect(reloadSpy).toHaveBeenCalled();
  });

  it('should set selectedPit', () => {
    const pitValue = 'C';
    component.getSelectedPit(pitValue);
    expect(component.selectedPit).toBe(pitValue);
  });

  it('should handle ngOnDestroy', () => {
    spyOn(component.subscription, 'unsubscribe');
    component.ngOnDestroy();
    expect(component.subscription.unsubscribe).toHaveBeenCalled();
  });
});
