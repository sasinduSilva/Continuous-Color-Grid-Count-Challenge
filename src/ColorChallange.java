import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;

public class ColorChallange {

    public static final char[] COLORS = {'R', 'B', 'W', 'G'};

    private HashMap<Coord, Node> game;

    private int col;
    private int row;

    public void initGame(int cols, int rows) {
        this.col = cols;
        this.row = rows;

        Random random = new Random();
        this.game = new HashMap<>();
        for (int i = 0; i < cols*rows ; i++ ) {
            int x = i % cols;
            int y = (int) Math.floor(i/cols);
            this.game.put(new Coord(x, y), new Node(i, x, y, COLORS[random.nextInt(3)]));
        }
    }

    public Node getNode(int x, int y) {
        return this.game.get(new Coord(x, y));
    }

    public void printGame() {
        for (int y = 0; y < row; y++) {
            for(int x = 0; x < col; x++) {
                if(x == this.col - 1 ) {
                    System.out.println(getNode(x, y).getColor());
                } else {
                    System.out.print(getNode(x, y).getColor() + ", ");
                }
            }
        }
    }

    public void printGameWithBlock(Block block) {
        for (int y = 0; y < row; y++) {
            for(int x = 0; x < col; x++) {
                Node n = getNode(x, y);
                char color = block.hasNode(n) ? '=' : n.getColor();
                if(x == this.col - 1 ) {
                    System.out.println(color);
                } else {
                    System.out.print(color + ", ");
                }
            }
        }
    }

    private List<Node> findNeighourNodes(Node n, Block block) {
        List<Node> nodes = new ArrayList<>();
        Coord coordinate = n.getCoordinate();
        Node north = this.game.get(coordinate.north());
        if (north != null && north.getColor() == n.getColor() && !block.hasNode(north)) {
            nodes.add(north);
        }
        Node south = this.game.get(coordinate.south());
        if (south != null && south.getColor() == n.getColor() && !block.hasNode(south)) {
            nodes.add(south);
        }
        Node east = this.game.get(coordinate.east());
        if (east != null && east.getColor() == n.getColor() && !block.hasNode(east)) {
            nodes.add(east);
        }
        Node west = this.game.get(coordinate.west());
        if (west != null && west.getColor() == n.getColor() && !block.hasNode(west)) {
            nodes.add(west);
        }
        return nodes;
    }

    public Block getContinousBlock(int x, int y) {
        Coord startCoord = new Coord(x, y);
        Node startNode = this.game.get(startCoord);
        Block block = new Block(startNode.getColor());
        block.addNode(startNode);

        LinkedList<Node> nodesToVisit = new LinkedList<>();
        nodesToVisit.addAll(findNeighourNodes(startNode, block));

        while(!nodesToVisit.isEmpty()) {
            Node nextNode = nodesToVisit.remove();
            block.addNode(nextNode);
            nodesToVisit.addAll(findNeighourNodes(nextNode, block));
        }

        return block;
    }

    public Block getLargestBlock() {
        Set<Coord> allCoords = new HashSet<>(this.game.keySet());
        List<Block> allBlocks = new ArrayList<>();
        while(!allCoords.isEmpty()) {
            Coord coord = allCoords.iterator().next();
            Block newBlock = getContinousBlock(coord.getX(), coord.getY());
            allBlocks.add(newBlock);
            allCoords.removeAll(newBlock.allCoords());
        }
        Collections.sort(allBlocks);
        return allBlocks.size() > 0 ? allBlocks.get(0) : null;
    }

    public static void main(String[] args) {
        int WIDTH = 60;
        int HEIGHT = 200;

        ColorChallange challenge = new ColorChallange();
        challenge.initGame(WIDTH, HEIGHT);
        challenge.printGame();

        long startTime = Instant.now().toEpochMilli();
        Block block = challenge.getLargestBlock();
        long endTime = Instant.now().toEpochMilli();

        if (block != null) {
            System.out.println();
            System.out.println();
            System.out.println(
                    MessageFormat.format("=============================== " +
                                    " FOUND LARGEST BLOCK IN {0}ms " +
                                    " =============================== ",
                            endTime - startTime));
            System.out.println();
            challenge.printGameWithBlock(block);
        }
    }

}
