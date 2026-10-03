import java.awt.*;

class KeepAwake {
    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        System.out.println("Keep-awake running... Press Ctrl+C to stop");

        while (true) {
            Point p = MouseInfo.getPointerInfo().getLocation();
            System.out.println(p);
            robot.mouseMove(p.x + 1, p.y);   // move 1 pixel
            System.out.println(p);
            robot.mouseMove(p.x, p.y);       // move back
            Thread.sleep(50_000);            // every 50 seconds
        }
    }
}