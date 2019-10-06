package utils.time;

public class TimeOutHolder {
    long start;
    long end;
    public TimeOutHolder(){}
    public void start(){
        start = System.currentTimeMillis();
    }
    public void end(){
        end = System.currentTimeMillis();
    }
    public void printElapsed(){
        long elapsed = end-start;
        System.out.println("Elapsed = " + elapsed);
    }
}
