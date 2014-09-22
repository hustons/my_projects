import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

enum Fill {
	NONE,
	X,
	O
}

enum BoardState {
	PLAY,
	WON,
	DRAW
}

public class Run {
    
    static boolean Debug = false;
    
    public static void main( String[] args ) {
    	
    	Board board = new Board(Debug);
    	Player player1;
    	Player player2;
    	
    	if (Debug) System.out.println("Board on startup:");
    	board.Print();
    	
    	try {
			InputData input = ReadInputData(args[0]);
			
			for (int i = 0; i < input.NumCases; i++) {
				if (Debug) System.out.println("Processing Case #" + (i+1));
				board.Clear();
				board.Init(input.BoardValues[i]);
				
				player1 = new Player(input.NextPlayer[i], Debug);
				if (input.NextPlayer[i] == Fill.X) {
					player2 = new Player(Fill.O, Debug);
				}
				else {
					player2 = new Player(Fill.X, Debug);
				}
				
				if (Debug) {
					System.out.println("Player1: " + player1.Name);
					System.out.println("Player2: " + player2.Name);
				}
				
				// Play the board until a win or draw
				while (board.State == BoardState.PLAY) {
					if (Debug) System.out.println("Taking a turn");
					if (board.CurrentPlayer == Fill.NONE || board.CurrentPlayer == player2.Name) {
						player1.Play(board);
					}
					else {
						player2.Play(board);
					}
					board.UpdateState();
				}
				
				// Output results
				if (board.State == BoardState.DRAW) {
					System.out.println("Case " + (i+1) + ": DRAW");
				}
				else {
					System.out.println("Case " + (i+1) + ": " + board.CurrentPlayer + " wins");
				}
			}
		}
		catch (IOException e) {
		}
    	
    }
    
    public static InputData ReadInputData(String path) throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader bf = new BufferedReader(fr);
		
		int numCases = Integer.parseInt(bf.readLine());
		InputData input = new InputData(numCases, Debug);
		
		for (int i = 0; i < numCases; i++) {
			char nextPlayer = bf.readLine().charAt(0);
			input.SetNextPlayer(i, nextPlayer);
			for (int j = 0; j < 3; j++) {
				input.BoardValues[i][j] = bf.readLine();
			}			
		}
		
		input.Print();
		return input;
	}

}

class InputData extends TicTacToe {
	public int NumCases;
	public String[][] BoardValues;
	public Fill[] NextPlayer;
	
	public InputData(int num, boolean debug) {
		Debug = debug;
		NumCases = num;
		BoardValues = new String[NumCases][3];
		NextPlayer = new Fill[3];
	}
	
	public void SetNextPlayer(int caseId, char player) {
		if (player == 'X') {
			NextPlayer[caseId] = Fill.X;
		}
		else {
			NextPlayer[caseId] = Fill.O;
		}
	}
	
	public void Print() {
		if (!Debug) return;
		System.out.println("Found " + NumCases + " cases.");
		
		for (int i = 0; i < NumCases; i++) {
			System.out.println("Case " + i + ":");
			System.out.println("\tNextPlayer: " + NextPlayer[i]);
			for (int j = 0; j < 3; j++) {
				System.out.println("\t" + BoardValues[i][j]);
			}
		}
	}
}		 

class TicTacToe {
	static boolean Debug;
	
	public TicTacToe(boolean debug) {
		Debug = debug;
	}
	
	public TicTacToe() {
	}
}

class Board extends TicTacToe {
	
	public static int Size = 3;
	static int CentreCellId = 1;
	public BoardState State;
	public Cell[][] Cells;
	public Fill CurrentPlayer;
		
	public Board(boolean debug) {
		Debug = debug;
		
		// Initialise an empty board
		Cells = new Cell[Size][Size];
		Clear();
	}
	
	public void Clear() {
		for (int i = 0; i < Size; i++) {
			for (int j = 0; j < Size; j++) {
				Cells[i][j] = new Cell();
			}
		}
		State = BoardState.PLAY;
		CurrentPlayer = Fill.NONE;
	}
	
	public void Init(String[] inputValues) {
		for (int i=0; i< Size; i++) {
			InitRow(i, inputValues[i]);
		}
		if (Debug) {
			System.out.println("Board after update:");
			Print();
		}
	}
	
	public void InitRow(int row, String values) {
		for (int i=0; i < Size; i++) {
			char value = values.charAt(i);
			if (value == 'X') {
				Cells[row][i].Value = Fill.X;
			}
			else if (value == 'O') {
				Cells[row][i].Value = Fill.O;
			}
		}
	}
	
	public void Winner() {
		if (Debug) System.out.println("WE HAVE A WINNER: " + CurrentPlayer);
		State = BoardState.WON;
	}
	
	public void UpdateState() {
		// Check for win
		for (int i = 0; i < Size; i++) {
			if (CollectionIsWin(GetRow(i)) || CollectionIsWin(GetColumn(i))) {
				Winner();
				break;
			} 
		}
		if (CollectionIsWin(GetDiagonal(false)) || CollectionIsWin(GetDiagonal(true))) {
			Winner();
		}
		
		// Check for draw
		if (IsDraw()) {
			State = BoardState.DRAW;
		}
	}
	
	public Cell[] ReverseCollection(Cell[] cells) {
		Cell[] result = new Cell[Size];
		
		int j = Size - 1;
		for (int i = 0; i < Size; i++) {
			result[j-i] = cells[i];
		}
		
		return result;
	}
	
	public void UpdatePlayer(Fill player) {
		CurrentPlayer = player;
	}
	
	public boolean CollectionIsWin(Cell[] collection) {
		return collection[0].Value != Fill.NONE && 
				collection[0].Value == collection[1].Value  && 
				collection[1].Value == collection [2].Value;
	}
	
	public boolean IsDraw() {
		for (int i = 0; i < Size; i++) {
			for (int j = 0; j < Size; j++) {
				if (Cells[i][j].Value == Fill.NONE) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Cell[] GetRow(int id) {
		return Cells[id];
	}
	
	public Cell[] GetColumn(int id) {
		Cell[] result = new Cell[Size];
		for (int i = 0; i < Size; i ++) {
			result[i] = Cells[i][id];
		}
		return result;		
	}
	
	public Cell[] GetDiagonal(boolean reverse) {
		Cell[] result = new Cell[Size];
		if (reverse) {
			int j = 0;
			for (int i = Size-1; i >=0; i--) {
				result[i] = Cells[j++][i];
			}
		}
		else {
			for (int i = 0; i < Size; i++) {
				result[i] = Cells[i][i];
			}
		}
		return result;
	}
	
	public Cell GetCentre() {
		return Cells[CentreCellId][CentreCellId];
	}
	
	public void Print() {
		if (!Debug) return;
		
		for (int i = 0; i < Size; i++) {
			for (int j = 0; j < Size; j++) {
				Cells[i][j].Print();
			}
			System.out.println();
		}
	}
}

class Cell {
	
	public Fill Value;
	public Cell() {
		Value = Fill.NONE;
	} 
	
	public boolean CompareTo(Fill fill) {
		return Value == fill;
	}
	
	public void Print() {
		switch(Value) {
			case NONE:
				System.out.print("_");
				break;
			default:
				System.out.print(Value);
			}
	}
} 

class Player extends TicTacToe {
	public Fill Name;
	public Fill Other;
	
	public Player(Fill name, boolean debug) {
		Name = name;
		if (Name == Fill.X) {
			Other = Fill.O;
		}
		else {
			Other = Fill.X;
		}
	}
	
	public void Play(Board board) {
		if (Debug) System.out.println("Play called for Player " + Name);
		
		board.UpdatePlayer(Name);
		if (TryWin(board)) return;
		if (BlockWin(board)) return;
		if (TryFork(board, Name)) return;
		if (TryFork(board, Other)) return;
		if (TakeCentre(board)) return;
		if (TakeOppositeCorner(board)) return;
		if (TakeEmptyCorner(board)) return;
		FillAnyEmptyCell(board);
	}
	
	public boolean TryFork(Board board, Fill match) {
		if (Debug) System.out.println("TryFork called for " + match);
		
		int max = board.Size - 1;
		
		// Four ways to fork
		if (TryFork(board.GetRow(0), board.GetColumn(0), match)) {		
			board.Print();
			return true; 
		}
		if (TryFork(board.ReverseCollection(board.GetRow(0)), 
				board.GetColumn(max), match)) {		
			board.Print();
			return true; 
		}
		if (TryFork(board.GetRow(max), 
				board.ReverseCollection(board.GetColumn(0)), match)) {		
			board.Print();
			return true; 
		}
		if (TryFork(board.ReverseCollection(board.GetRow(max)), 
				board.ReverseCollection(board.GetColumn(max)), match)) {		
			board.Print();
			return true; 
		}		
		return false;
	}
	
	public boolean TryFork(Cell[] row, Cell[] col, Fill match) {
		if (!CollectionMatchesForkCondition(row, match)) {
			return false;
		}
				
		if (!CollectionMatchesForkCondition(col, match)) {
			return false;
		}
		row[0].Value = Name;
		return true;
	}
	
	public boolean CollectionMatchesForkCondition(Cell[] cells, Fill match) {
		
		//Ensure that the common cell is empty
		if (cells[0].Value != Fill.NONE) {
			return false;
		}
		
		// Ensure that one of the other cells is empty
		if (cells[1].Value != Fill.NONE && cells[2].Value != Fill.NONE) {
			return false;
		}
		
		// Ensure other cells are not equal
		if (cells[1].Value == cells[2].Value) {
			return false;
		}
		
		// Ensure at least one of the other cells matches the desired value
		if (cells[1].Value != match && cells[2].Value != match) {
			return false;
		}
		
		
		return true;
	}
	
	public boolean TakeEmptyCorner(Board board) {
		if (Debug) System.out.println("TakeEmptyCorner called for " + Name);
		if (TakeEmptyCorner(board.GetDiagonal(false), board.Size)) {
			board.Print();
			return true;
		}
		if (TakeEmptyCorner(board.GetDiagonal(true), board.Size)) {
			board.Print();
			return true;
		}
		return false;
	}
	
	public boolean TakeEmptyCorner(Cell[] cells, int size) {
		if (cells[0].Value == Fill.NONE) {
			cells[0].Value = Name;
			return true;
		}
		int max = size - 1;
		if (cells[max].Value == Fill.NONE) {
			cells[max].Value = Name;
			return true;
		}
		return false;
	}
	
	public boolean TakeOppositeCorner(Board board) {
		if (Debug) System.out.println("TakeOppositeCorner called for " + Name);
		if (TakeOppositeCorner(board.GetDiagonal(false), board.Size)) {
			board.Print();
			return true;
		}
		if (TakeOppositeCorner(board.GetDiagonal(true), board.Size)) {
			board.Print();
			return true;
		}
		return false;
	}
	
	public boolean TakeOppositeCorner(Cell[] cells, int size) {
		int max = size-1;		
		if (cells[0].Value == Other && cells[max].Value == Fill.NONE) {
			cells[max].Value = Name;
			return true;
		}
		if (cells[0].Value == Fill.NONE && cells[max].Value == Other) {
			cells[0].Value = Name;
			return true;
		}
		return false;
	}
	
	public boolean TakeCentre(Board board) {
		if (Debug) System.out.println("TakeCentre called for " + Name);
		Cell cell = board.GetCentre();
		if (cell.Value == Fill.NONE) {
			cell.Value = Name;
			board.Print();
			return true;
		}
		return false;
	}
	
	public void FillAnyEmptyCell(Board board) {
		for (int i=0; i<board.Size; i++) {
			for (int j=0; j<board.Size; j++) {
				Cell cell = board.Cells[i][j];
				if (cell.Value == Fill.NONE) {
					cell.Value = Name;
					board.Print();
					return;
				}
			}
		}
	}					
	
	public boolean TryWin(Board board) {
		return ProcessWin(board, Name);
	}
	
	public boolean BlockWin(Board board) {
		return ProcessWin(board, Other);
	}
	
	public boolean ProcessWin(Board board, Fill match) {		
		if (Debug) System.out.println("ProcessWin called for Player " + Name);
		
		if (ProcessWinCollection(board.GetDiagonal(false), match) || 
			ProcessWinCollection(board.GetDiagonal(true), match)) {
				board.Print();
				return true;
		}
		
		for (int i = 0; i < board.Size; i++) {
			if (ProcessWinCollection(board.GetRow(i), match) || 
				ProcessWinCollection(board.GetColumn(i), match)) {
					board.Print();
					return true;
			}
		}

		return false;
	}
	
	public boolean ProcessWinCollection(Cell[] cells, Fill match) {
		if (cells[0].CompareTo(Fill.NONE) && 
			cells[1].CompareTo(match) && 
			cells[2].CompareTo(match)) {
				cells[0].Value = Name;
				return true;
			}
		if (cells[0].CompareTo(match) && 
			cells[1].CompareTo(Fill.NONE) && 
			cells[2].CompareTo(match)) {
				cells[1].Value = Name;
				return true;
			}
		if (cells[0].CompareTo(match) && 
			cells[1].CompareTo(match) && 
			cells[2].CompareTo(Fill.NONE)) {
				cells[2].Value = Name;
				return true;
			}
		return false;
	}
}
