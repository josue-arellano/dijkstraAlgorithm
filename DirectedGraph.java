/*************************************************************** 
*   file: Vertex.java 
*   author: J. Arellano 
*   class: CS 241 – Data Structures and Algorithms II
* 
*   assignment: program 3
*   date last modified: 3/8/2018 
* 
*   purpose: This file implements the VertexInterface interface.
*            this file is used to create vertices that are used to hold
*            city data and help us find shortest paths.
* 
****************************************************************/
package directedgraph;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;

public class DirectedGraph<T, V> implements GraphInterface<T, V>
{
    private DictionaryInterface<T, VertexInterface<V>> vertices;
    private int edgeCount;
    
    public DirectedGraph()
    {
        vertices = new LinkedDictionary<>();
        edgeCount = 0;
    }
    
    //method:  getLabel
    //purpose: This method returns the VertexInterfaces label by calling the 
    //         getValue method.
    public V getLabel(T vertexKey)
    {
        return vertices.getValue(vertexKey).getLabel();
    }
    
    //method:  addVertex
    //purpose: This method adds a new dictionary entry with the input key and
    //         a new Vertex created with the input label.
    public boolean addVertex(T vertexKey, V label)
    {
        VertexInterface<V> addOutcome = vertices.add(vertexKey, 
                                                     new Vertex<V>(label));
        
        return addOutcome == null;
    }
    
    //method:  addEdge
    //purpose: This method finds the vertices begin and end in the vertices 
    //         field and makes sure that they are not null. If they are not null
    //         then adds a connection between the begin vertex and the end
    //         vertex. Finally it adds one to the edgeCount.
    public boolean addEdge(T begin, T end, double edgeWeight)
    {
        boolean result = false;
        
        VertexInterface<V> beginVertex = vertices.getValue(begin);
        VertexInterface<V> endVertex = vertices.getValue(end);
        
        if((beginVertex != null) && (endVertex != null))
            result = beginVertex.connect(endVertex, edgeWeight);
        
        if(result)
            edgeCount++;
        
        return result;
    }
    
    //method:  getCheapestPath
    //purpose: This method creates a new priority queue and populates it with
    //         vertices. It iterates through all the vertices and visits each
    //         of its neighbors. It reorders the vertices with the priority 
    //         queue to find the shortest edges to each vertex. It finds the 
    //         cheapest path to each node and sets the path to it and the cost.
    //         Once all the vertices are visited, the method finds the end
    //         vertex and sets visits its predecessor until the begin vertex is
    //         found. As it traverses all the predcessors, it pushes them onto
    //         the stack.
    public double getCheapestPath(T origin, T end, Stack<VertexInterface<V>> path)
    {
        boolean done = false;
        PriorityQueue<VertexInterface<V>> priority = new 
                                            PriorityQueue<VertexInterface<V>>();
        VertexInterface<V> originVertex = vertices.getValue(origin);
        VertexInterface<V> endVertex = vertices.getValue(end);
        Iterator<VertexInterface<V>> vertexIterator = 
                                            vertices.getValueIterator();
        
        
        while(vertexIterator.hasNext())
        {
            VertexInterface<V> currentVertex = vertexIterator.next();
            currentVertex.setCost(Double.POSITIVE_INFINITY);
        }
        originVertex.setCost(0);
        priority.add(originVertex);
        
        while(!done && !priority.isEmpty())
        {
            VertexInterface<V> frontVertex = priority.remove();
            if(frontVertex == endVertex)
            {
                done = true;
            } 
            else
            {
                Iterator<VertexInterface<V>> neighbors = 
                        frontVertex.getNeighborIterator();
                while(neighbors.hasNext())
                {
                    VertexInterface<V> nextNeighbor = neighbors.next();
                    double weightOfEdge = ((Vertex<V>)frontVertex).findEdge(
                                                      nextNeighbor).getWeight();
                    if(!nextNeighbor.isVisited())
                    {
                        double nextCost = weightOfEdge + frontVertex.getCost();
                        if(nextCost < nextNeighbor.getCost())
                        {
                            nextNeighbor.setCost(nextCost);
                            nextNeighbor.setPredecessor(frontVertex);
                        }
                        priority.add(nextNeighbor);
                    }
                }
            }
            frontVertex.visit();
        }
        double pathCost = endVertex.getCost();
        path.push(endVertex);
        
        VertexInterface<V> currentVertex = endVertex;
        while(currentVertex.hasPredecessor())
        {
            currentVertex = currentVertex.getPredecessor();
            path.push(currentVertex);
        }
        resetVertices();
        return pathCost;
    }
    
    //method:  addEdge
    //purpose: This method calls the overloaded method addEdge.
    public boolean addEdge(T begin, T end)
    {
        return addEdge(begin, end, 0);
    }
    
    //method:  hasEdge
    //purpose: This method iterates through the neighbors of begin to see if 
    //         end is one of its neighbors. If end is a neighbor, it returns
    //         true, if not, the method returns false.
    public boolean hasEdge(T begin, T end)
    {
        boolean found = false;
        VertexInterface<V> beginVertex = vertices.getValue(begin);
        VertexInterface<V> endVertex = vertices.getValue(end);
        
        if((beginVertex != null) && (endVertex != null))
        {
            Iterator<VertexInterface<V>> neighbors =
                    beginVertex.getNeighborIterator();
            while(!found && neighbors.hasNext())
            {
                VertexInterface<V> nextNeighbor = neighbors.next();
                if(endVertex.equals(nextNeighbor))
                    found = true;
            }
        }
        
        return found;
    }
    
    //method:  removeEdge
    //purpose: This method disconnects the end vertex from the begin vertex and
    //         if the edge between them exists. It then subtracts one from the 
    //         edgeCount.
    public boolean removeEdge(T begin, T end)
    {
        boolean found = false;
        
        VertexInterface<V> beginVertex = vertices.getValue(begin);
        VertexInterface<V> endVertex = vertices.getValue(end);
        
        if(hasEdge(begin, end))
        {
            found = beginVertex.disconnect(endVertex);
        }
        
        if(found)
            edgeCount--;
        return found;
    }
    
    //method:  isEmpty
    //purpose: This method calls isEmpty of vertices.
    public boolean isEmpty()
    {
        return vertices.isEmpty();
    }
    
    //method:  clear
    //purpose: This method clears vertices and sets 0 to edgeCount.
    public void clear()
    {
        vertices.clear();
        edgeCount = 0;
    }
    
    //method:  getNumberOfVertices
    //purpose: This method returns the size of vertices.
    public int getNumberOfVertices()
    {
        return vertices.getSize();
    }
    
    //method:  getNumberOfEdges
    //purpose: This method returns the edgeCount.
    public int getNumberOfEdges()
    {
        return edgeCount;
    }
    
    //method:  resetVertices
    //purpose: This method iterates through the vertices. It unvisits, sets cost
    //         to zero, and sets the predecessor to null for all the vertices.
    protected void resetVertices()
    {
        Iterator<VertexInterface<V>> vertexIterator = 
                                     vertices.getValueIterator();
        while(vertexIterator.hasNext())
        {
            VertexInterface<V> nextVertex = vertexIterator.next();
            nextVertex.unvisit();
            nextVertex.setCost(0);
            nextVertex.setPredecessor(null);
        }
    }
}
