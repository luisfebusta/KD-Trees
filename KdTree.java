import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class KdTree {
    private Node set;
    private int size;

    public KdTree()
    {
        // construct an empty set of points 
        set = null;
        size = 0;
    }
    public boolean isEmpty()
    {
        // is the set empty?
        return set == null;

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

        if (set == null)
        {
            set = new Node(p);
            size++;
            return;
        }

        //if level is odd compare x axis if even compare y axis 
        int level = 0;

        Comparator<Point2D> cmp;

        Node current = set;

        while (current != null)
        {
            //if point is already in the set don't do anything
            if (current.equals(p))
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


            if (cmp.compare(p, current.point) <= 0)
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

    }
    public boolean contains(Point2D p)   
    {
        // does the set contain point p? 
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");

        if (set == null)
        {
            return false;
        }

        //if level is odd compare x axis if even compare y axis 
        int level = 0;

        Node current = set;
        Comparator<Point2D> cmp;

        while (current != null)
        {
            //if point is already in the set don't do anything
            if (current.equals(p))
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

            if (cmp.compare(p, current.point) <= 0)
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
    public void draw()                
    {
        // draw all points to standard draw
        draw(set);

    }
    private void draw(Node n)
    {
        if (n == null)
            return;

        draw(n.left);
        n.point.draw();
        draw(n.right);

    }
    public Iterable<Point2D> range(RectHV rect)   
    {
        if (rect == null)
            throw new  java.lang.NullPointerException("Argument can't be null");


        Set<Point2D> result = new TreeSet<Point2D>();

        int level = -1;

        Node current = set;
        Comparator<Point2D> cmp = null;
        rangeHelper(rect, current, result, level, cmp);

        return result;
    }

    private void rangeHelper (RectHV rect, Node current, Set<Point2D> result, int level, Comparator<Point2D> cmp)
    {
        if (current == null)
            return;

        level = ++level % 2;

        // if rectangle contains current point need to search both sides and add
        // point to the result set
        if (rect.contains(current.point))
        {
            result.add(current.point);  
            rangeHelper(rect, current.left, result, level, cmp);
            rangeHelper(rect, current.right, result, level, cmp);
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



        if (cmp.compare(new Point2D(rect.xmin(), rect.ymin()), current.point) <= 0)
        {
            rangeHelper(rect, current.left, result, level, cmp);
        }
        else
        {
            rangeHelper(rect, current.right, result, level, cmp);
        }

    }
    public Point2D nearest(Point2D p)   
    {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null)
            throw new  java.lang.NullPointerException("Argument can't be null");

        if (isEmpty())
            return null;

        Point2D nearest = set.point;
        Comparator<Point2D> cmp = null;
        // double minDist = p.distanceTo(nearest.point);
        nearestHelper(p, set, nearest, -1, cmp);

        return nearest;
    }

    private void nearestHelper(Point2D p, Node current, Point2D nearest, int level, Comparator<Point2D> cmp)
    {
        if (current == null)
            return;

        level = ++level % 2;

        Point2D thisNearest = nearest;


        // if rectangle contains current point need to search both sides and add
        // point to the result set

        if (p.distanceTo(current.point) <= p.distanceTo(nearest))
        {
            nearest = current.point;
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


        //compare x axis
        if (cmp.compare(p, current.point) <= 0)
        {
            nearestHelper(p, current.left, nearest, level, cmp);
            if (thisNearest.equals(nearest))
            {
                nearestHelper(p, current.right, nearest, level, cmp); 
            }
        }
        else
        {
            nearestHelper(p, current.right, nearest, level, cmp);
            if (thisNearest.equals(nearest))
            {
                nearestHelper(p, current.left, nearest, level, cmp); 
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