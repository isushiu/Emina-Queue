import java.util.ArrayList;

public class Queue
{
    protected ArrayList list;
    public Queue()
    {
        list = new ArrayList();
    }
    
    public boolean isEmpty()
    {
        return list.isEmpty();
    }
    
    public int size()
    {
        return list.size();
    }
    
    public void enqueue(Object element)
    {
        list.add(element);
    }
    
    public Object dequeue()
    {
        return list.remove(0);
    }
    
    public Object front()
    {
        return list.get(0);
    }
}

