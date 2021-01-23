import javafx.scene.shape.Circle;

public class GameCamera
{
   private float xOffset, yOffset, screenWidth, screenHeight;
   // TODO Store a zoom variable?
   public GameCamera(float screenWidth, float screenHeight, float xOffset, float yOffset)
   {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
   }
   
   public void move(float xAmount, float yAmount)
   {
      xOffset += xAmount;
      yOffset += yAmount;
   }
   
   public void centerOn(Circle circle)
   {
      xOffset = ((float)circle.getCenterX() - screenWidth / 2 );
      yOffset = ((float)circle.getCenterY() - screenHeight / 2 );
   }
   
   public void centerOn(float x, float y)
   {
      xOffset = (x - screenWidth / 2 );
      yOffset = (y - screenHeight / 2 );
   }
   
   public float getxOffset()
   {
      return xOffset;
   }
   
   public float getyOffset()
   {
      return yOffset;
   }
   
   public void setxOffset(float n)
   {
      xOffset = n;
   }
   
   public void setyOffset(float n)
   {
      yOffset = n;
   }
   
}