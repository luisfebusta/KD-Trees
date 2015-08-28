import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> set;

    public PointSET()
    {
        // construct an empty set of points 
        set = new TreeSet<Point2D>();
    }
    public boolean isEmpty()
    {
        // is the set empty?
        return set.isEmpty();

    }
    public int size()   
    {
        // number of points in the set
        return set.size();
    }
    public void insert(Point2D p)   
    {
        // add the point to the set (if it is not already in the set)
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");
        set.add(p);
    }
    public boolean contains(Point2D p)   
    {
        // does the set contain point p? 
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");
        return set.contains(p);
    }
    public void draw()                
    {
        // draw all points to standard draw
        for (Point2D p : set)
        {
            p.draw();
        }
    }
    public Iterable<Point2D> range(RectHV rect)   
    {
        // all points that are inside the rectangle
        if (rect == null)
            throw new  java.lang.NullPointerException("Argument can't be null");
        
        TreeSet<Point2D> result = new TreeSet<Point2D>();
        for (Point2D p : set)
        {
            if (rect.contains(p))
                result.add(p);
        }
        return result;
    }
    public Point2D nearest(Point2D p)   
    {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");
        
        if (set.isEmpty())
            return null;
        
        Point2D nearest = null;
        
        for (Point2D s : set)
        {
            if (nearest == null || p.distanceSquaredTo(s) 
                    < p.distanceSquaredTo(nearest))
            {
                nearest = s;
            }
        }
        
        return nearest;
    }

    public static void main(String[] args)     
    {
        // unit testing of the methods (optional) 
    }
}
