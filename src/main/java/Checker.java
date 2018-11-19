public class Checker
{
    private boolean isBlack;
    private int currentRow;
    private int currentCol;
    private Board board;

    public Checker(Board board, boolean isBlack, int row, int column)
    {
        this.board = board;
        this.isBlack = isBlack;
        currentRow = row;
        currentCol = column;
    }

    public boolean isBlack()
    {
        return isBlack;
    }

    public int getRow()
    {
        return currentRow;
    }

    public int getColumn()
    {
        return currentCol;
    }

    public Cell getCell()
    {
        return board.getCellAt(currentRow, currentCol);
    }
}
