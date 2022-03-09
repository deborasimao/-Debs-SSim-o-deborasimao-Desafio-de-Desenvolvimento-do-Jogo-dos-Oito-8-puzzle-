import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//Desafio de Desenvolvimento do Jogo dos Oito (8-puzzle) Debora Simao
public class GameOfEight extends JPanel { // A grande terá um painel desenhado
  
	private static final long serialVersionUID = 1L;
///Tamanho do nosso jogo
  private int size;
  ////Número de quadrados
  private int nbTiles;
  //Dimensão da interface do usuário da grade
  private int dimension;
  // Cor do primeiro plano
  private static final Color FOREGROUND_COLOR = new Color(0,127,255); // usei cores similares ao site bem paggo
  // Objeto aleatório para embaralhar as peças
  private static final Random RANDOM = new Random();
  // Armazenando os tiles em um array 1D de inteiros
  private int[] tiles;
  // Tamanho do bloco na interface do usuário
  private int tileSize;
  // Posição do azulejo em branco
  private int blankPos;
  //Margem para a grade no quadro
  private int margin;
  //Tamanho da interface do usuário da grade
  private int gridSize;
  private boolean gameOver; // true se o jogo acabou!!ou  false caso contrário
  
  public GameOfEight(int size, int dim, int mar) {
    this.size = size;
    dimension = dim;
    margin = mar;
    
    // blocos para inicialização
    nbTiles = size * size - 2; // -1 porque não contamos o bloco em branco ( será o bloco branco
    tiles = new int[size * size];
    
    // calcular o tamanho da grade e o tamanho do quadrado
    gridSize = (dim - 1 * margin);
    tileSize = gridSize / size;
    
    setPreferredSize(new Dimension(dimension, dimension + margin));
    setBackground(Color.WHITE);
    setForeground(FOREGROUND_COLOR);
    setFont(new Font("SansSerif", Font.BOLD, 60));
    
    gameOver = true;
    
    addMouseListener(new MouseAdapter() {
      
      public void mousePressed(MouseEvent e) {
        //implementaçao e interação com os usuários para mover as peças para resolver o jogo.
        if (gameOver) {
          newGame();
        } else {
        	// obtém a posição do clique


          int ex = e.getX() - margin;
          int ey = e.getY() - margin;
          
          //// clica na grade ?
          if (ex < 0 || ex > gridSize  || ey < 0  || ey > gridSize)
            return;
          
       // obtém posição na grade
          int c1 = ex / tileSize;
          int r1 = ey / tileSize;
          
       // obtém a posição da célula em branco

          int c2 = blankPos % size;
          int r2 = blankPos / size;
          
       // convertemos na coord 1D
          int clickPos = r1 * size + c1;
          
          int dir = 0;
          
       // buscamos a direção para vários movimentos de peças de uma só vez
          if (c1 == c2  &&  Math.abs(r1 - r2) > 0)
            dir = (r1 - r2) > 0 ? size : -size;
          else if (r1 == r2 && Math.abs(c1 - c2) > 0)
            dir = (c1 - c2) > 0 ? 1 : -1;
            
          if (dir != 0) {
        	// mover  os tiles na direção
            do {
              int newBlankPos = blankPos + dir;
              tiles[blankPos] = tiles[newBlankPos];
              blankPos = newBlankPos;
            } while(blankPos != clickPos);
            
            tiles[blankPos] = 0;
          }
          
          // verificamos se o jogo foi resolvido
          gameOver = isSolved();
        }
            
        repaint();
      }
    });
    
    newGame();
  }
  
  private void newGame() {
    do {
      reset(); // redefinir no estado inicial
      shuffle(); // embaralhar
    } while(!isSolvable()); // faz até que a grade seja solucionável
   
    gameOver = false;
  }
  
  private void reset() {
    for (int i = 0; i < tiles.length; i++) {
      tiles[i] = (i + 1) % tiles.length;
    }
    
    //definimos uma célula em branco no último
    blankPos = tiles.length - 1;
  }
  
  private void shuffle() {
	// não inclui o tile em branco no shuffle, deixa na posição resolvida
    int n = nbTiles;
    
    while (n > 1) {
      int r = RANDOM.nextInt(n--);
      int tmp = tiles[r];
      tiles[r] = tiles[n];
      tiles[n] = tmp;
    }
  }
  
  /// Apenas meias permutações do quebra-cabeça são solucionáveis
  // Sempre que um tile é precedido por um tile de maior valor ele conta
  // como uma inversão. Nesse caso, com o ladrilho em branco na posição resolvida,
  // o número de inversões deve ser par para que o quebra-cabeça seja solucionado
  private boolean isSolvable() {
    int countInversions = 0;
    
    for (int i = 0; i < nbTiles; i++) {
      for (int j = 0; j < i; j++) {
        if (tiles[j] > tiles[i])
          countInversions++;
      }
    }
    
    return countInversions % 2 == 0;
  }
  
  private boolean isSolved() {
    if (tiles[tiles.length - 1] != 0) // se o bloco em branco não estiver na posição resolvida ==> não resolvido
      return false;
    
    for (int i = nbTiles - 1; i >= 0; i--) {
      if (tiles[i] != i + 1)
        return false;      
    }
    
    return true;
  }
  
  private void drawGrid(Graphics2D g) {
    for (int i = 0; i < tiles.length; i++) {
      // /convertemos as coordenadas 1D em coordenadas 2D dado o tamanho da matriz 2D


      int r = i / size;
      int c = i % size;
      // convertemos em coordenadas na interface do usuário
      int x = margin + c * tileSize;
      int y = margin + r * tileSize;
      
      /// verifica o caso especial do bloco em branco
      if(tiles[i] == 0) {
        if (gameOver) {
          g.setColor(FOREGROUND_COLOR);
          drawCenteredString(g, "\u2713", x, y);
        }
        
        continue;
      }
      
   // para outros ladrilhos
      g.setColor(getForeground());
      g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
      g.setColor(Color.BLACK);
      g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
      g.setColor(Color.WHITE);
      
      drawCenteredString(g, String.valueOf(tiles[i]), x , y);
    }
  }
  
  private void drawStartMessage(Graphics2D g) {
    if (gameOver) {
      g.setFont(getFont().deriveFont(Font.BOLD, 18));
      g.setColor(FOREGROUND_COLOR);
      String s = "Click to start new game";
      g.drawString(s, (getWidth() - g.getFontMetrics().stringWidth(s)) / 2,
          getHeight() - margin);
    }
  }
  
  private void drawCenteredString(Graphics2D g, String s, int x, int y) {
	  // centraliza a string s para o tile dado (x,y)
    FontMetrics fm = g.getFontMetrics();
    int asc = fm.getAscent();
    int desc = fm.getDescent();
    g.drawString(s,  x + (tileSize - fm.stringWidth(s)) / 2, 
        y + (asc + (tileSize - (asc + desc)) / 2));
  }
  
 
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2D = (Graphics2D) g;
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g2D);
    drawStartMessage(g2D);
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setTitle("Desafio de Desenvolvimento do Jogo dos Oito (8-puzzle)DeboraS simão ");
      frame.setResizable(false);
      frame.add(new GameOfEight(3, 550, 30), BorderLayout.CENTER);
      frame.pack();
   // centralizando na tela
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }

  
}