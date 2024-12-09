public abstract class Piece {
	private boolean isWhite;

	public Piece(boolean isWhite) {
		this.isWhite = isWhite;
	}

	public boolean isWhite() {
		return isWhite;
	}

	public abstract Piece copy();

	public abstract boolean isValidMove(int fromX, int fromY, int toX, int toY, Piece[][] board, boolean forRendering);
}