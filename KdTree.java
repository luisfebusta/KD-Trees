import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class KdTree {
    private Node head;
    private int size;

    public KdTree()
    {
        // construct an empty set of points 
        head = null;
        size = 0;
    }
    public boolean isEmpty()
    {
        // is the set empty?
        return head == null;

    }
    public int size()   
    {
        // number of points in the set
        return size;
    }
    public void insert(Point2D p)   
    {
        // add the point to the set (if it is not already in the set)
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");

        if (head == null)
        {
            head = new Node(p);
            size++;
            return;
        }

        //if level is even compare x axis if odd compare y axis 
        int level = 0;

        Comparator<Point2D> cmp;

        Node current = head;

        while (current != null)
        {
            //if point is already in the set don't do anything
            if (current.point.equals(p))
            {
                return;
            }


            if (level == 0)
            {
                //compare x axis
                cmp = Point2D.X_ORDER;
            }
            else
            {
                //compare y axis
                cmp = Point2D.Y_ORDER;
            }


            if (cmp.compare(p, current.point) < 0)
            {
                if (current.left != null)
                {
                    current = current.left;
                }
                else
                {
                    current.left = new Node(p);
                    break;
                }
            }
            else
            {
                if (current.right != null)
                {
                    current = current.right;
                }
                else
                {
                    current.right = new Node(p);
                    break;
                }
            }

            level = ++level % 2;
        }
        size++;
    }
    public boolean contains(Point2D p)   
    {
        // does the set contain point p? 
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");

        if (head == null)
        {
            return false;
        }

        //if level is odd compare x axis if even compare y axis 
        int level = 0;

        Node current = head;
        Comparator<Point2D> cmp;

        while (current != null)
        {
            //if point is found return true.
            if (current.point.equals(p))
            {
                return true;
            }

            if (level == 0)
            {
                //compare x axis
                cmp = Point2D.X_ORDER;
            }
            else
            {
                //compare y axis
                cmp = Point2D.Y_ORDER;
            }

            if (cmp.compare(p, current.point) < 0)
            {
                current = current.left;
            }
            else
            {
                current = current.right;
            }

            level = ++level % 2;
        }

        return false;
    }
    
    private class Boundaries
    {
        private double xmin;
        private double xmax;
        private double ymin;
        private double ymax;
        
        public Boundaries(double xmin, double ymin, double xmax, double ymax)
        {
            this.xmin = xmin;
            this.xmax = xmax;
            this.ymin = ymin;
            this.ymax = ymax;
        }
        public Boundaries copy()
        {
            return new Boundaries(xmin, ymin, xmax, ymax);
        }
        
    }
    
    public void draw()                
    {
        Node n = head;
        Boundaries b = new Boundaries(0.0, 0.0, 1.0, 1.0);
        draw(n, b, false);
    }
    
    private void draw(Node n, Boundaries b, boolean horizontal)
    {
        if (n == null)
            return;
        //draw initial dot and vertical line 
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        n.point.draw();
        StdDraw.setPenRadius();
        Boundaries bLeft = b.copy();
        Boundaries bRight = b.copy();
        if (!horizontal) // vertical
        {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.point.x(), b.ymin, n.point.x(), b.ymax);

            bLeft.xmax = n.point.x();
            bRight.xmin = n.point.x();
            
            draw(n.left, bLeft, !horizontal);
            draw(n.right, bRight, !horizontal);
        }
        else //horizontal
        {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(b.xmin, n.point.y(), b.xmax, n.point.y());
            
            bLeft.ymax = n.point.y();
            bRight.ymin = n.point.y();
            
            draw(n.left, bLeft, !horizontal);
            draw(n.right, bRight, !horizontal);
            
        }

    }
    public Iterable<Point2D> range(RectHV rect)   
    {
        if (rect == null)
            throw new  java.lang.NullPointerException("Argument can't be null");


        Set<Point2D> result = new TreeSet<Point2D>();

        int level = -1;

        Node current = head;
        rangeHelper(rect, current, result, level);

        return result;
    }

    private void rangeHelper(RectHV rect, Node current,
            Set<Point2D> result, int lvl)
    {
        if (current == null)
            return;

        int level = (lvl +1) % 2;

        // if rectangle contains current point need to search both sides and add
        // point to the result set
        if (rect.contains(current.point))
        {
            result.add(current.point);  
            rangeHelper(rect, current.left, result, level);
            rangeHelper(rect, current.right, result, level);
            return;
        }  
        Comparator<Point2D> cmp;
        if (level == 0)
        {
            //compare x axis
            cmp = Point2D.X_ORDER;
        }
        else
        {
            //compare y axis
            cmp = Point2D.Y_ORDER;
        }



        if (cmp.compare(new Point2D(rect.xmin(), rect.ymin()), current.point) < 0)
        {
            rangeHelper(rect, current.left, result, level);
        }
        
        if (cmp.compare(new Point2D(rect.xmax(), rect.ymax()), current.point) >= 0)
        {
            rangeHelper(rect, current.right, result, level);
        }
        
    }
    public Point2D nearest(Point2D p)   
    {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");

        if (isEmpty())
            return null;
        
       


        NearestNode nearest = new NearestNode();
        nearest.point = head.point;
        nearest.dSquared = p.distanceSquaredTo(nearest.point);
        Boundaries b = new Boundaries(0.0, 0.0, 1.0, 1.0);
        nearestHelper(p, head, nearest, false, b);
        

        return nearest.point;
    }
    
    //used to pass nearest point back and forth between 
    private class NearestNode {
        private Point2D point;
        private double dSquared;
    }

    private void nearestHelper(Point2D p, Node current, NearestNode nearest,
            boolean xCompare,
            Boundaries b)
    {
        if (current == null)
            return;

        boolean xCmp = !xCompare;

        //NearestNode thisNearest = nearest;
        
        double dSquaredToCurrent =  p.distanceSquaredTo(current.point);
       
        if (dSquaredToCurrent < nearest.dSquared)
        {
            nearest.point = current.point;
            nearest.dSquared = dSquaredToCurrent;
            if (nearest.point.compareTo(p) == 0) // if same point.
                return;
        }
        
        Comparator<Point2D> cmp;
        Boundaries bLeft = b.copy();
        Boundaries bRight = b.copy();
        RectHV rect;
        
        if (xCmp)
        {
          //compare x axis
            cmp = Point2D.X_ORDER;
            bLeft.xmax = current.point.x();
            bRight.xmin =  current.point.x();
        }
        else
        {
            //compare y axis
            cmp = Point2D.Y_ORDER;
            bLeft.ymax = current.point.y();
            bRight.ymin = current.point.y();
        }
        
        if (cmp.compare(p, current.point) < 0)
        {
            nearestHelper(p, current.left, nearest, xCmp, bLeft);
            //need to create rectangle that holds all the the 
            //points in right subtree
            //and compare distance to nearest.dSquared
            rect = new RectHV(bRight.xmin, bRight.ymin, bRight.xmax, bRight.ymax);
            if (nearest.dSquared > rect.distanceSquaredTo(p))
            {
                nearestHelper(p, current.right, nearest, xCmp, bRight); 
            }
        }
        else
        {
            nearestHelper(p, current.right, nearest, xCmp, bRight);
            rect = new RectHV(bLeft.xmin, bLeft.ymin, bLeft.xmax, bLeft.ymax);
            if (nearest.dSquared > rect.distanceSquaredTo(p))
            {
                nearestHelper(p, current.left, nearest, xCmp, bLeft);
            }
        }

    }

    private class Node
    {
        private Point2D point;
        private Node left;
        private Node right;

        public Node(Point2D p)
        {
            point = p;
            left = null;
            right = null;
        }

    }

    public static void main(String[] args)     
    {
        // unit testing of the methods (optional) 
    }
}