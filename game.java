//COMP30024 PROJECT A
//Group memeber:
//Name:Yitao Deng  login id:yitaod    stu id:711645
//Name:Shang Chen  login id:shangc1   stu id:650034


//a two-player board game AI under specific rules via improved Minimax searching and α-β Pruning
//Promoted the behavior of AI by machine learning via optimizing evaluation functions and search strategies

import java.io.*;

//Coordinate class which contains X, Y coordinates and a method to return a copy of itself
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

//Edge class which contains its own coordinates, coordinates of cells which it belongs to, its current state and copy method as well. 
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
		edgecopy.edgeSelfCoordinate=coordinateC.copyCoordC(edge.edgeSelfCoordinate);
		for(int i=0;i<2;i++)
		{
			edgecopy.edgesCellCoordinate[i]=coordinateC.copyCoordC(edge.edgesCellCoordinate[i]);
		}
		return edgecopy;
	} 
}

//Cell class similar to the edge one, containing its capture state and 6 edges; 
//but with extra methods to check whether it's captured and the possibility of capturing in the next move
class cellC
{
	char cellState;
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
			cellcopy.cellsEdge[i]=edgeC.copyEdgeC(cell.cellsEdge[i]);  //????havenot linked with edge[][]
		}
		return cellcopy;		 
	}
	//return how many edges are occupied by 'B' or 'R' in this cell
	public int cellsEdgeOccupiedNumber()
	{
		int counter=0;
		for(int i=0;i<6;i++)
		{
			if (this.cellsEdge[i].edgeState=='R'||this.cellsEdge[i].edgeState=='B')
			counter++;
		}
		return counter;
	}
	
	//set cellState according to its edges'State, also return the cellState
	public char setCellState()
	{
		if (this.cellsEdgeOccupiedNumber()==6)
		{
			this.cellState='C';
		}
		else
		{
			this.cellState='F';
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

//The class of game contains methods to read input and to build game board accordingly 
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
				//link Hexicopy.cell[][].cellsEdge[] with Hexicopy.edege[][], according to the coordinate relationship between cell and edge
				Hexicopy.cell[i][j].cellsEdge[0]=Hexicopy.edge[2*i][2*j];
				Hexicopy.cell[i][j].cellsEdge[1]=Hexicopy.edge[2*i][2*j+1];
				Hexicopy.cell[i][j].cellsEdge[2]=Hexicopy.edge[2*i+1][2*j];
				Hexicopy.cell[i][j].cellsEdge[3]=Hexicopy.edge[2*i+1][2*j+2];
				Hexicopy.cell[i][j].cellsEdge[4]=Hexicopy.edge[2*i+2][2*j+1];
				Hexicopy.cell[i][j].cellsEdge[5]=Hexicopy.edge[2*i+2][2*j+2];
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
				edge[i][j].edgesCellCoordinate[0].setCoordinate(-1,-1);		//set coordinate of cell, which edge belongs to, as (-1,-1) at first
				edge[i][j].edgesCellCoordinate[1].setCoordinate(-1,-1);
			}
		}
		for(int i = 0; i < 2*dim-1; i++ )
		{
			for(int j = 0; j < 2*dim-1; j++ )
			{
				cell[i][j]= new cellC();
				//link cell[][].cellsEdge[] with edege[][], according to the coordinate relationship between cell and edge
				cell[i][j].cellsEdge[0]=edge[2*i][2*j];
				cell[i][j].cellsEdge[1]=edge[2*i][2*j+1];
				cell[i][j].cellsEdge[2]=edge[2*i+1][2*j];
				cell[i][j].cellsEdge[3]=edge[2*i+1][2*j+2];
				cell[i][j].cellsEdge[4]=edge[2*i+2][2*j+1];
				cell[i][j].cellsEdge[5]=edge[2*i+2][2*j+2];
				for(int k = 0; k<6 ; k++)
				{
					if(cell[i][j].cellsEdge[k].edgesCellCoordinate[0].validCoordinate()==false) //means this edge havenot assigned to any cell
					{
						cell[i][j].cellsEdge[k].edgesCellCoordinate[0].setCoordinate(i,j);	//assign this edge to its firt cell
					}
					else
					{
						cell[i][j].cellsEdge[k].edgesCellCoordinate[1].setCoordinate(i,j);   //assign this edge to its second cell
					}
				}
			}
		}
	}
	//Method to read inputs from command line inputs or command line inputs file via applying BufferedReader class and InputStreamReader
	public void readIO() throws IOException
	{
		//Read input of size of board to be generated
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		String str = null;
		dim = Integer.parseInt(br.readLine());  //read the dimension from first line
		//Call method of board construction with given size
		this.consBoard();
		//Load state from command line input into edge array
		for(int i = 0; i < 4*dim-1; i++)
		{
			//Read command line input end with newline(enter) once, and do it repetitively untill the whole board is filled
			str = br.readLine();
			//check whether inputs satisfy pre-defined size
			if(str != null && str.length() == 8*dim-3 )
			{
				for(int j = 0 ; j < 4*dim-1; j++)
				{
					edge[i][j].edgeState = str.charAt(2*j);
					//check if the inputs are valid characters
					if(edge[i][j].edgeState != 'B' && edge[i][j].edgeState != 'R' && edge[i][j].edgeState != '+' && edge[i][j].edgeState != '-')
					{
						System.out.println("Syntax Error");
						System.exit(0);
					}
				}
			}
			else
			{
				System.out.println("Syntax Error");
				System.exit(0);
			}
			
		}
		//exit in case that inputs exceeds pre-defined size
		if(br.readLine() != null)
		{
			System.out.println("Syntax Error");
			System.exit(0);
		}
	}
	
	//print all edgeState in standard form; it will be utilized to output current board in future project
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
	
	//method to set all cell's cellState
	public void setAllCell()
	{
		for(int i = 0; i < 2*dim-1; i++ )
		{
			for(int j = 0; j < 2*dim-1; j++ )
			{
				cell[i][j].setCellState();
			}
		}
	}
	
	//method to return the number of possible moves under this board configuration
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
	
	//method to return the number of cells available for capture by one move
	public int numOfCellCanBeCap()
	{
		int counter=0;
		for(int i=0;i<2*dim-1;i++)
		{
			for(int j=0;j<2*dim-1;j++)
			{
				if (this.cell[i][j].cellToBeCaptured()==true)
				{
					counter++;
				}
			}
		}
		return counter;
	}
	
	//method to return how many cells are captured under a given move; 
	//edgeMoved is the edge changed in this given move
	public int cellCapByOneMove(edgeC edgeMoved)
	{
		Hexifence nextHexi= new Hexifence();
		//a move will change the state of Hexifence; in order to leave the original one unchanged,
		//we deeply copy it to another Hexifence-nextHexi, and do this move in nextHexi
		nextHexi=this.copyHexi(this);
		int counter=0;
		int i,j;
		//get the coordinate of edgeMoved
		i=edgeMoved.edgeSelfCoordinate.x;
		j=edgeMoved.edgeSelfCoordinate.y;
		//changing the edgeState means doing this move
		nextHexi.edge[i][j].edgeState='R'; //R or B dont matter in this case
		int[] m=new int[2];
		int[] n=new int[2];
		//also need to get which cells that this edge belongs to
		for(int k=0;k<2;k++)
		{
			m[k]=nextHexi.edge[i][j].edgesCellCoordinate[k].x;
			n[k]=nextHexi.edge[i][j].edgesCellCoordinate[k].y;		 
			if (m[k]!=-1)  //cell's coordinate ==-1 means this cell is not exist 
			{
				if(nextHexi.cell[ m[k] ] [ n[k] ].setCellState()=='C') // cellState changes from 'F'(Free) to 'C'(Captured)
				{
					counter++;		//means one cell captured by this move
				}
			}
		}
		return counter;
	}
	
	//method to return max number of cells which can be captured by one move
	public int maxNumCellToBeCapByOneMove()
	{
		int temp=0;
		int max=0;
		for(int i=0;i<4*dim-1;i++)
		{
			for(int j=0;j<4*dim-1;j++)
			{
				if (this.edge[i][j].edgeState=='+')
				{
					//get the answer that how many cells are captured under a given move
					temp=cellCapByOneMove(this.edge[i][j]);
				}
				if (temp>max)
				{
					max=temp;
				}
				if (max==2)
				{
					return max;
				}
			}
		}
		return max;
	}
}

public class game
{

	public static void main(String[] args) throws IOException
	{
		Hexifence game = new Hexifence();
		
		game.readIO();			//read the board configuration and set each edgeState
		game.setAllCell();		//set all cellState according to edgeState
		System.out.println(game.numOfPossMove());
		System.out.println(game.maxNumCellToBeCapByOneMove());
		System.out.println(game.numOfCellCanBeCap());
	}
}






