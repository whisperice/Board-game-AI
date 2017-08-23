//a two-player board game AI under specific rules via improved Minimax searching and α-β Pruning
//Promoted the behavior of AI by machine learning via optimizing evaluation functions and search strategies


//Group memeber:
//Name:Yitao Deng  login id:yitaod    stu id:711645
//Name:Shang Chen  login id:shangc1   stu id:650034


package aiproj.hexifence;
//import aiproj.hexifence.*;
import java.io.*;
import java.util.*;

class coordinateC
{
	int x,y;
	public boolean validCoordinate()
	{
		if(x>=0 && y>=0)
		return true;
		else 
		return false;
	}
	public void setCoordinate(int a, int b)
	{
		x=a;
		y=b;
	}
	//deeply copy a coordinateC class
	public static coordinateC copyCoordC(coordinateC coord)
	{
		coordinateC coordcopy= new coordinateC();
		coordcopy.x=coord.x;
		coordcopy.y=coord.y;
		return coordcopy;
	} 
}

//Edge class which contains its own coordinates, coordinates of cells 
//which it belongs to, its current state and copy method as well. 
class edgeC
{
	char edgeState;
	coordinateC edgeSelfCoordinate=new coordinateC();
	coordinateC[] edgesCellCoordinate = new coordinateC[2];
	{
		for(int i = 0; i<2 ; i++)
		{
			edgesCellCoordinate[i] = new coordinateC();
		}
	}
	
	//deeply copy an edgeC class	 
	public static edgeC copyEdgeC(edgeC edge)
	{
		edgeC edgecopy=new edgeC();
		edgecopy.edgeState=edge.edgeState;
		edgecopy.edgeSelfCoordinate=
		coordinateC.copyCoordC(edge.edgeSelfCoordinate);
		for(int i=0;i<2;i++)
		{
			edgecopy.edgesCellCoordinate[i]=
			coordinateC.copyCoordC(edge.edgesCellCoordinate[i]);
		}
		return edgecopy;
	} 
}

//Cell class similar to the edge one, containing its capture state
// and 6 edges; but with extra methods to check whether it's captured
// and the possibility of capturing in the next move
class cellC
{
	char cellState='-';
	edgeC[] cellsEdge = new edgeC[6];
	{
		for(int i = 0; i<6 ; i++)
		{
			cellsEdge[i] = new edgeC();
		}
	}
	//deeply copy a cellC class
	public static cellC copyCellC(cellC cell)
	{
		cellC cellcopy=new cellC();
		cellcopy.cellState=cell.cellState;
		for(int i=0;i<6;i++)
		{
			cellcopy.cellsEdge[i]=edgeC.copyEdgeC(cell.cellsEdge[i]);  
		}
		return cellcopy;		 
	}
	//return how many edges are occupied by 'B' or 'R' in this cell
	public int cellsEdgeOccupiedNumber()
	{
		int counter=0;
		for(int i=0;i<6;i++)
		{
			if(this.cellsEdge[i].edgeState=='-')
			{
				return -1;
			}
			if (this.cellsEdge[i].edgeState=='R'||
			this.cellsEdge[i].edgeState=='B')
			{
				counter++;
			}
		}
		return counter;
	}
	
	//set cellState according to its edges'State, also return the cellState
	public char setCellState(char cellS)
	{
		if (this.cellsEdgeOccupiedNumber()==6)
		{
			this.cellState=cellS;
		}
		return this.cellState;
	}
	
	//check whether the cell will be captured in the next move
	public boolean cellToBeCaptured()
	{
		if (this.cellsEdgeOccupiedNumber()==5)
		return true;
		else
		return false;
	} 	 
}

//The class of game contains methods to read input and 
//to build game board accordingly 
class Hexifence
{
	int dim;
	edgeC[][] edge;
	cellC[][] cell;
	
	//deeply copy a Hexifence class
	public Hexifence copyHexi(Hexifence Hexi)
	{ 
		Hexifence Hexicopy=new Hexifence();
		Hexicopy.dim=Hexi.dim;
		Hexicopy.edge = new edgeC[4*dim-1][4*dim-1];
		Hexicopy.cell = new cellC[2*dim-1][2*dim-1];
		for(int i=0;i<4*dim-1;i++)
		{
			for(int j=0;j<4*dim-1;j++)
			{
				Hexicopy.edge[i][j]=edgeC.copyEdgeC(Hexi.edge[i][j]);
			}				
		}
		for(int i=0;i<2*dim-1;i++)
		{
			for(int j=0;j<2*dim-1;j++)
			{
				Hexicopy.cell[i][j]=cellC.copyCellC(Hexi.cell[i][j]);
				//link Hexicopy.cell[][].cellsEdge[] with Hexicopy.edege[][],
				//according to the coordinate relationship between cell 
				//and edge
				Hexicopy.cell[i][j].cellsEdge[0]=
				Hexicopy.edge[2*i][2*j];
				Hexicopy.cell[i][j].cellsEdge[1]=
				Hexicopy.edge[2*i][2*j+1];
				Hexicopy.cell[i][j].cellsEdge[2]=
				Hexicopy.edge[2*i+1][2*j];
				Hexicopy.cell[i][j].cellsEdge[3]=
				Hexicopy.edge[2*i+1][2*j+2];
				Hexicopy.cell[i][j].cellsEdge[4]=
				Hexicopy.edge[2*i+2][2*j+1];
				Hexicopy.cell[i][j].cellsEdge[5]=
				Hexicopy.edge[2*i+2][2*j+2];
			}				
		}
		return Hexicopy;		 
	}	 
	//construction of the Board, including some initialization
	public void consBoard()
	{
		edge = new edgeC[4*dim-1][4*dim-1];
		cell = new cellC[2*dim-1][2*dim-1];			 
		for(int i = 0; i < 4*dim-1; i++ )
		{
			for(int j = 0; j < 4*dim-1; j++ )
			{
				edge[i][j] = new edgeC();
				edge[i][j].edgeSelfCoordinate.setCoordinate(i,j); 
				//set coordinate of cell, which edge belongs to, 
				//as (-1,-1) at first
				edge[i][j].edgesCellCoordinate[0].setCoordinate(-1,-1);	
				edge[i][j].edgesCellCoordinate[1].setCoordinate(-1,-1);
			}
		}
		for(int i = 0; i < 2*dim-1; i++ )
		{
			for(int j = 0; j < 2*dim-1; j++ )
			{
				cell[i][j]= new cellC();
				//link cell[][].cellsEdge[] with edege[][], according to
				//the coordinate relationship between cell and edge
				cell[i][j].cellsEdge[0]=edge[2*i][2*j];
				cell[i][j].cellsEdge[1]=edge[2*i][2*j+1];
				cell[i][j].cellsEdge[2]=edge[2*i+1][2*j];
				cell[i][j].cellsEdge[3]=edge[2*i+1][2*j+2];
				cell[i][j].cellsEdge[4]=edge[2*i+2][2*j+1];
				cell[i][j].cellsEdge[5]=edge[2*i+2][2*j+2];
				for(int k = 0; k<6 ; k++)
				{
					//means this edge havenot assigned to any cell
					if(cell[i][j].cellsEdge[k].edgesCellCoordinate[0]
					.validCoordinate()==false) 
					{
						//assign this edge to its firt cell
						cell[i][j].cellsEdge[k].edgesCellCoordinate[0]
						.setCoordinate(i,j);	
					}
					else
					{
						//assign this edge to its second cell
						cell[i][j].cellsEdge[k].edgesCellCoordinate[1]
						.setCoordinate(i,j);   
					}
				}
			}
		}
		for(int i = 0; i < 4*dim-1; i++ )
		{
			if(i < 2*dim)
			{
				for(int j = 0; j < 4*dim-1; j++ )
				{
					if(j < 2*dim+i && i%2 == 0)
					{
						edge[i][j].edgeState = '+';
					}
					else if (j < 2*dim+i && i%2 == 1)
					{
						if(j%2 == 0)
						{
							edge[i][j].edgeState = '+';
						}
						else
						{
							edge[i][j].edgeState = '-';
						}
					}
					else
						{
							edge[i][j].edgeState = '-';
						}
				}
			}
			else
			{
				for(int j = 0; j < 4*dim-1; j++ )
				{
					if(j > i - 2*dim && i%2 == 0)
					{
						edge[i][j].edgeState = '+';
					}
					else if (j > i - 2*dim && i%2 == 1)
					{
						if(j%2 == 0)
						{
							edge[i][j].edgeState = '+';
						}
						else
						{
							edge[i][j].edgeState = '-';
						}
					}
					else
						{
							edge[i][j].edgeState = '-';
						}
				}
			}
			
		}
	}

	
	//print all edgeState in standard form; it will be utilized to 
	//output current board in future project
	public void printEdge()
	{
		for(int i = 0; i < 4*dim-1; i++)
		{
			for(int j = 0; j < 4*dim-1; j++)
			{
				System.out.printf("%c",this.edge[i][j].edgeState);
			}
			System.out.println("");
		}
	}
	
	//method to return the number of possible moves 
	//under this board configuration
	public int numOfPossMove()
	{
		int counter=0;
		for(int i=0;i<4*dim-1;i++)
		{
			for(int j=0;j<4*dim-1;j++)
			{
				if (this.edge[i][j].edgeState=='+')
				{
					counter++;
				}
			}
		}
		return counter;
	}
	

}


//the class required by Part B
public class yitaod implements Player, Piece
{
	//constants contain dimension of board 
	// and which player this class belongs to 
	public int boardSize = 0;
	public char playerPiece = 0,oppoPiece = 0;
	public Hexifence game = new Hexifence();
	
	//some constants used by searching and pruning
	public int maxDepth=3;
	public double aOfPruning=Double.NEGATIVE_INFINITY;
	public double bOfPruning=Double.POSITIVE_INFINITY;
	public Move previousMove = new Move();
	
	//weights used by evaluation function
	public double w1=0.0;
	public double w2=0.0;
	public double w3=0.0;
	
	//function to initialize the player board
	//apply different weight to evaluation function 
	//according to the player sequence
	public int init(int n, int p)
	{
		this.boardSize = n;
		game.dim = n;
		game.consBoard();
		if(boardSize==2)
		{
			maxDepth+=3;
		}
		if(p == 1)
		{
			this.playerPiece = 'B';
			this.oppoPiece = 'R';
			this.w1=2.5;
			this.w2=7.0;
			this.w3=23.0;
			return 1;
		}
		else if (p == 2)
		{
			this.playerPiece = 'R';
			this.oppoPiece = 'B';
			this.w1=2.5;
			this.w2=7.0;
			this.w3=22.0;
			return 1;
		}
		else
		{
			return -1;
		}
	}
	
	//function to return a move after called
	public Move makeMove()
	{
		Move m = new Move();
		//do first move; When the board is empty, there is no need to 
		//do deep search, so just return the move 
		//at left uo corner to save time;
		if(game.numOfPossMove()==9*boardSize*boardSize-3*boardSize)	
		{
			Move iniMove= new Move();
			coordinateC cod= new coordinateC();
			cod.setCoordinate(0,0);
			iniMove=this.codToMove(cod,1);
			previousMove=iniMove;
			game.edge[0][0].edgeState = 'B';
			return iniMove;
		}
		else
		{
			m=miniMaxWithAB(game);
			previousMove=m;
			//the player needs to update its board after each move
			game.edge[m.Row][m.Col].edgeState = playerPiece;
			for(int i=0;i<2;i++)
			{
				int j,k;
				j=game.edge[m.Row][m.Col].edgesCellCoordinate[i].x;
				k=game.edge[m.Row][m.Col].edgesCellCoordinate[i].y;
				if(j!=-1)
				{
					char oldState;
					oldState = game.cell[j][k].cellState;
					game.cell[j][k].setCellState((char)(playerPiece+32));
					if(oldState != game.cell[j][k].cellState)
					{
						game.edge[2*j+1][2*k+1].edgeState = 
						(char)(playerPiece+32);
					}
				}
			}
			return m;
		}
	}
	
	//function to return the evaluation value of the input state;
	//previous move is needed, because if cell is captured by previous
	//move, the player will remain the same
	public double evaluationFuncValue(Hexifence nowState,Move premove)
	{
		double evaluationFuncValue=0.0;
		int numberFour=0,numberFive=0;
		int numberb=0,numberr=0;
		int occupiedNumber=0;
		int signForFive=0,signForFour=0;
		for(int i = 0; i < 2*boardSize-1; i++ )
		{
			for(int j = 0; j < 2*boardSize-1; j++ )
			{
				occupiedNumber=nowState.cell[i][j].cellsEdgeOccupiedNumber();
				if(occupiedNumber==-1)	//it is not an available cell
				{
					break;
				}
				if(occupiedNumber==4)	//this cell has 4 edges occupied
				{
					
					numberFour++;
				}
				if(occupiedNumber==5)	//this cell has 5 edges occupied
				{
					numberFive++;
				}
			}
		}
		numberb=bNum(nowState);		//number of cell captured by blue
		numberr=rNum(nowState);		//number of cell captured by red
		if(cellCapByPreMove(nowState,premove)==true)
		{
			if(premove.P==1)	//the Five (cell which has 5 edges occupied)
								// can be captured by blue;
								//the Four (cell which has 4 edges occupied) 
								//may be captured by red
			{
				signForFive=1;
				signForFour=-1;
			}
			else				//the Five can be captured by red;
								// the Four may be captured by blue
			{
				signForFive=-1;
				signForFour=1;
			}
		}
		else
		{
			if(premove.P==1)	//the Fivecan be captured by red;
								// the Four may be captured by blue
			{
				signForFive=-1;
				signForFour=1;
			}
			else				//the Five can be captured by blue;
								// the Four may be captured by red
			{
				signForFive=1;
				signForFour=-1;
			}
		}
		//because of the additional move, the evaluation value of cells, 
		//which have 4 or 5 edges occupied, depends on the previous move;
		//when previous move capture a cell, the Five is good for previous 
		//player;when the move dont capture a cell, the Five is bad for
		//previous player; Thus, we define signs for the Four and the Five,
		//and apply + or - to them according to diffierent conditions
		evaluationFuncValue=w1*signForFour*numberFour+
		w2*signForFive*numberFive+w3*numberb-w3*numberr;
		return evaluationFuncValue;
	}

	//function to give a rootstate, according to a nowState and a given move
	public Hexifence getRootState(Hexifence nowState,Move Action)
	{
		Hexifence nextState = new Hexifence();
		nextState=nowState.copyHexi(nowState);
		char playerOfAction =  Action.P==1?'B':'R';
		nextState.edge[Action.Row][Action.Col].edgeState=playerOfAction;
		for(int i=0;i<2;i++)
		{
			int m,n;
			char oldState;
			m=nextState.edge[Action.Row][Action.Col]
			.edgesCellCoordinate[i].x;
			n=nextState.edge[Action.Row][Action.Col]
			.edgesCellCoordinate[i].y;
			if(m!=-1)
			{
				oldState=nextState.cell[m][n].cellState;
				nextState.cell[m][n].setCellState((char)(playerOfAction+32));
				if(oldState != nextState.cell[m][n].cellState)
				{
					nextState.edge[2*m+1][2*n+1].edgeState = 
					(char)(playerOfAction+32);
				}
			}
		}
		return nextState;
	}

	//function to check whether nowState reach a terminal state
	public boolean getTerminal(Hexifence nowState)
	{
		for(int i = 0; i < 4*boardSize-1; i++ )
		{
			for(int j = 0; j < 4*boardSize-1; j++ )
			{
				if(nowState.edge[i][j].edgeState == '+')
				{
					return false;
				}
			}
		}
		return true;
	}

	//function to check whether nowState go to a depth need to cut off
	//for searching; the cutoff depth differs according to different dimesion
	//of board or percentange of occupied edges in board.
	public boolean cutOffTest(Hexifence nowState,int depth)
	{
		int numOfNowPossMove=game.numOfPossMove();
		int numOfMaxPossMove=9*boardSize*boardSize-3*boardSize;
		int percentOfBoard=(numOfMaxPossMove-numOfNowPossMove)*
		10/numOfMaxPossMove;
		int depthModified=maxDepth+percentOfBoard*2/9;
		if(percentOfBoard>=9&&boardSize<=3)
		{
			if(getTerminal(nowState)==true)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(depth==depthModified||getTerminal(nowState)==true)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	//funtion to combine the input coordinate and player and output a Move
	public Move codToMove(coordinateC cod,int nowPlayer)
	{
		Move result = new Move();
		result.P = nowPlayer;
		result.Row = cod.x;
		result.Col = cod.y;
		return result;
	}
	
	//function to check whether there is a cell captured by the premove 
	public boolean cellCapByPreMove(Hexifence nowState,Move premove)
	{
		for(int i=0;i<2;i++)
		{
			int j,k;
			j=nowState.edge[premove.Row][premove.Col]
			.edgesCellCoordinate[i].x;
			k=nowState.edge[premove.Row][premove.Col]
			.edgesCellCoordinate[i].y;
			if(j!=-1)
			{
				if(nowState.cell[j][k].cellState!='-')
				{
					return true;
				}
			}
		}
		return false;
	}
	
	//function to return a List of Move; this list contains all available
	//moves according to nowState
	public List<Move> getAvailableMoves(Hexifence nowState,Move premove)
	{
		List<Move> availableMoves = new ArrayList<>();
		int nowPlayer=0;
		for(int i = 0; i < 4*boardSize-1; i++ )
		{
			for(int j = 0; j < 4*boardSize-1; j++ )
			{
				if(nowState.edge[i][j].edgeState == '+')
				{
					Move newMove = new Move();
					if(cellCapByPreMove(nowState,premove)==true)
					{
						nowPlayer=premove.P;	//dont change player
					}
					else
					{
						//change from 1 to 2 or 2 to 1;means change player
						nowPlayer=3-premove.P;	
					}
					newMove = codToMove(nowState.edge[i][j]
					.edgeSelfCoordinate,nowPlayer);
					availableMoves.add(newMove);
				}
			}
		}
		return availableMoves;
	}

	//function to do minimax search with alpha-beta pruning; 
	//it is the core of searching strategy.
	public Move miniMaxWithAB(Hexifence nowState)
	{
		List<Move> availableMoves = new ArrayList<>();
		availableMoves=getAvailableMoves(nowState,previousMove);
		double miniMaxValueWhenMoveFirst=Double.NEGATIVE_INFINITY;
		double miniMaxValueWhenMoveSecond=Double.POSITIVE_INFINITY;
		aOfPruning=Double.NEGATIVE_INFINITY;
		bOfPruning=Double.POSITIVE_INFINITY;
		double rootValue=0.0;
		Move optimalMove = new Move();
		Move moveToRoot = new Move();
		Hexifence rootState =  new Hexifence();
		for(int i=0;i<availableMoves.size();i++)
		{
			moveToRoot=availableMoves.get(i);
			rootState=getRootState(nowState,moveToRoot);
			boolean capByMoveToRoot=cellCapByPreMove(rootState,moveToRoot);		
			if(playerPiece=='B')
			{	
				//now player is blue
				//If the Move moveToRoot dont cause a cell captured, 
				//the minimax value of roots should be min(player changed);
				//If the Move moveToRoot do capture a cell,
				//the minimax value of roots should be max(player unchanged)
				if(capByMoveToRoot==false)
				{
					rootValue=minValueOfState(rootState,moveToRoot,0);
				}
				else
				{
					rootValue=maxValueOfState(rootState,moveToRoot,0);
				}
				//the player is blue. Find the max value from
				//all rootstates. Also change the alpha of pruning.
				if(miniMaxValueWhenMoveFirst<rootValue)
				{
					miniMaxValueWhenMoveFirst=rootValue;
					optimalMove=moveToRoot;
					aOfPruning=Math.max(aOfPruning,
					miniMaxValueWhenMoveFirst);
				}
			}
			if(playerPiece=='R')
			{
				//now player is red
				//If the Move moveToRoot dont cause a cell captured, 
				//the minimax value of roots should be max(player changed);
				//If the Move moveToRoot do capture a cell,
				//the minimax value of roots should be min(player unchanged)
				if(capByMoveToRoot==false)
				{
					rootValue=maxValueOfState(rootState,moveToRoot,0);
				}
				else
				{
					rootValue=minValueOfState(rootState,moveToRoot,0);
				}
				// the player is red, find the min value between 
				//all rootstates.Also change the beta of pruning.
				if(miniMaxValueWhenMoveSecond>rootValue)
				{
					miniMaxValueWhenMoveSecond=rootValue;
					optimalMove=moveToRoot;
					bOfPruning=Math.min(bOfPruning,
					miniMaxValueWhenMoveSecond);
				}
			}
		}
		return optimalMove;
	}

	//one of the recursion function to do minimax search;
	//return the max rootvalue as the minimax value of nowState
	public double maxValueOfState(Hexifence nowState,Move premove,int depth)
	{
		//when cutoff, return the evalution function value of nowState
		if (cutOffTest(nowState,depth)==true)
		{
			return evaluationFuncValue(nowState,premove);
		}
		double miniMaxValue=Double.NEGATIVE_INFINITY;
		double rootValue=0.0;
		List<Move> availableMoves = new ArrayList<>();
		availableMoves=getAvailableMoves(nowState,premove);
		Move moveToRoot = new Move();
		Hexifence rootState = new Hexifence();
		for(int i=0;i<availableMoves.size();i++)
		{
			moveToRoot=availableMoves.get(i);
			rootState=getRootState(nowState,moveToRoot);
			boolean capByPreMove=cellCapByPreMove(rootState,moveToRoot);
			//when cell isnot captured by the Move moveToRoot, 
			//the player is changed, so the rootValue should change to min;
			//when cell is captured by the Move moveToRoot, 
			//the player is unchanged,so the rootValue should remain max;
			if(capByPreMove==false)
			{
				rootValue=minValueOfState(rootState,moveToRoot,depth+1);
			}
			else
			{
				rootValue=maxValueOfState(rootState,moveToRoot,depth+1);
			}
			miniMaxValue=Math.max(miniMaxValue,rootValue);
			//do pruning when the value of rootstate is bigger than beta
			if(miniMaxValue>=bOfPruning)
			{
				return miniMaxValue;
			}
			//update alpha with the max value of root			
			aOfPruning=Math.max(aOfPruning,miniMaxValue);
		}
		return miniMaxValue;
	}

	//the other recursion function to do minimax search
	//return the min rootvalue as the minimax value of nowState
	public double minValueOfState(Hexifence nowState,Move premove,int depth)
	{
		//when cutoff, return the evalution function value of nowState		
		if (cutOffTest(nowState,depth)==true)
		{
			return evaluationFuncValue(nowState,premove);
		}
		double miniMaxValue=Double.POSITIVE_INFINITY;
		double rootValue=0.0;
		List<Move> availableMoves = new ArrayList<>();
		availableMoves=getAvailableMoves(nowState,premove);
		Move moveToRoot = new Move();
		Hexifence rootState = new Hexifence();
		for(int i=0;i<availableMoves.size();i++)
		{
			moveToRoot=availableMoves.get(i);
			rootState=getRootState(nowState,moveToRoot);
			boolean capByPreMove=cellCapByPreMove(rootState,moveToRoot);
			//when cell isnot captured by the Move moveToRoot, 
			//the player is changed, so the rootValue should change to max;
			//when cell is captured by the Move moveToRoot, 
			//the player is unchanged,so the rootValue should remain min;
			if(capByPreMove==false)
			{
				rootValue=maxValueOfState(rootState,moveToRoot,depth+1);
			}
			else
			{
				rootValue=minValueOfState(rootState,moveToRoot,depth+1);
			}
			miniMaxValue=Math.min(miniMaxValue,rootValue);
			//do pruning when the value of rootstate is smaller than alpha
			if(miniMaxValue<=aOfPruning)
			{
				return miniMaxValue;
			}
			//update beta with the min value of root
			bOfPruning=Math.min(bOfPruning,miniMaxValue);
		}
		return miniMaxValue;
	}
	
	//apply opponent's move to internal board after it receive a move from referee
	public int opponentMove(Move m)
	{
		//return -1 if the coordinate is illegal
		if(game.edge[m.Row][m.Col].edgeState != '+')
		{
			return -1;
		}
		previousMove=m;
		int cap = 0;
		//update board
		game.edge[m.Row][m.Col].edgeState = oppoPiece;
		//update internal cell state
		for(int i=0;i<2;i++)
		{
			int j,k;
			j=game.edge[m.Row][m.Col].edgesCellCoordinate[i].x;
			k=game.edge[m.Row][m.Col].edgesCellCoordinate[i].y;
			if(j!=-1)
			{
				char oldState;
				oldState = game.cell[j][k].cellState;
				//set cell state
				game.cell[j][k].setCellState((char)(oppoPiece+32));
				if(oldState != game.cell[j][k].cellState)
				{
					//check if there is a capture
					game.edge[2*j+1][2*k+1].edgeState = 
					(char)(oppoPiece+32);
					cap++;
				}
			}
		}
		//return 1 if there is a capture, return 0 else
		if(cap > 0)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

	
	//return number of cells captured by red
	public int rNum(Hexifence nowState)
	{
		int r = 0;
		for(int i = 0; i < 4*boardSize-1; i++ )
		{
			for(int j = 0; j < 4*boardSize-1; j++ )
			{
				if(nowState.edge[i][j].edgeState == 'r')
				{
					r++;
				}
			}
		}
		return r;
	}
	
	//return number of cells captured by blue
	public int bNum(Hexifence nowState)
	{
		int r = 0;
		for(int i = 0; i < 4*boardSize-1; i++ )
		{
			for(int j = 0; j < 4*boardSize-1; j++ )
			{
				if(nowState.edge[i][j].edgeState == 'b')
				{
					r++;
				}
			}
		}
		return r;
	}

	//return the winner of game if a terminal state is reached
	public int getWinner()
	{
		int b = 0, r = 0;
		for(int i = 0; i < 4*boardSize-1; i++ )
		{
			for(int j = 0; j < 4*boardSize-1; j++ )
			{
				//return 0 if game de not yet done
				if(this.game.edge[i][j].edgeState == '+')
				{
					return 0;
				}
				//count number of each pieces
				else if(this.game.edge[i][j].edgeState == 'b')
				{
					b++;
				}
				else if(this.game.edge[i][j].edgeState == 'r')
				{
					r++;
				}
			}
		}
		if( b > r)
		{
			return 1;
		}
		else if (b < r)
		{
			return 2;
		}
		else if (b == r)
		{
			return 3;
		}
		return -1;
	}
	
	//print the game board
	public void printBoard(PrintStream output)
	{
		for(int i = 0; i < 4*boardSize-1; i++)
		{
			for(int j = 0; j < 4*boardSize-1; j++)
			{
				char c=game.edge[i][j].edgeState;
				if(c!='-')
				{
					output.printf("%c",c);
				}
				else
				{
					output.printf("%c",c);
				}
			}
			output.println("");
		}
	}


}