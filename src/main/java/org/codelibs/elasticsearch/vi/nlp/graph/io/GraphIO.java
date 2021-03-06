/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.graph.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.graph.AdjacencyListGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.AdjacencyListWeightedGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.AdjacencyMatrixGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.Edge;
import org.codelibs.elasticsearch.vi.nlp.graph.IGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.IWeightedGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.util.GraphUtilities;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 18, 2007, 10:08:21 PM
 *         <p>
 *         The single class that provides methods for graph I/O.
 */
public final class GraphIO {

    private static final Logger logger = LogManager.getLogger(GraphIO.class);

    /**
     * The graph is directed or not.
     */
    static final boolean DIRECTED = true;

    /**
     * Constructs a graph from an adjacency list provided by a reader.
     * @param reader a reader
     * @see #scanAdjacencyList(String)
     * @return a graph
     */
    public static IGraph scanAdjacencyList(final Reader reader) {
        IGraph graph = null;
        final BufferedReader br = new BufferedReader(reader);
        try {
            // read the number of vertices of the graph
            // that is specified on the first line.
            final int n = Integer.parseInt(br.readLine());
            // logger.info(n);
            if (n > 0) {
                // create a graph with n vertices
                graph = new AdjacencyListGraph(n, DIRECTED);
            } else {
                logger.error("The number of vertices of the graph must be positive.");
                System.exit(1);
            }
            // read edges of the graph
            String line = "";
            while ((line = br.readLine()) != null && line.trim().length() > 0) {
                final String[] uv = line.split("\\s+");
                if (uv.length != 2) {
                    logger.error("Bad format for data input stream!");
                    System.exit(1);
                }
                final int u = Integer.parseInt(uv[0]);
                final int v = Integer.parseInt(uv[1]);
                // insert a new edge to the graph
                graph.insert(new Edge(u, v));
            }
            // close the isr
            br.close();
        } catch (final IOException e) {
            logger.warn(e);
        }
        return graph;
    }

    /**
     * Scans a graph from a text data file name. The data file is organised as
     * follows:
     * <ul>
     * <li>The first line is the number of vertices of the graph</li>
     * <li>The other lines contains couples u v (separated by at least a blank char)
     * that represent edges of the graph.</li>
     * </ul>
     *
     * @param filename
     * @see #scanAdjacencyList(Reader)
     * @return an adjacency list graph.
     */
    public static IGraph scanAdjacencyList(final String filename) {
        IGraph graph = null;
        try {
            final InputStreamReader isr = new FileReader(filename);
            graph = scanAdjacencyList(isr);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        }
        return graph;
    }

    /**
     * Scans a graph from a reader.
     *
     * @see #scanAdjacencyMatrix(String)
     * @return an adjacency matrix graph.
     */
    public static IGraph scanAdjacencyMatrix(final Reader reader) {
        IGraph graph = null;
        final BufferedReader br = new BufferedReader(reader);
        try {
            // read the number of vertices of the graph
            // that is specified on the first line.
            final int n = Integer.parseInt(br.readLine());
            // logger.info(n);
            if (n > 0) {
                // create a graph with n vertices
                graph = new AdjacencyMatrixGraph(n, DIRECTED);
            } else {
                logger.error("The number of vertices of the graph must be positive.");
                System.exit(1);
            }
            // read edges of the graph
            String line = "";
            int u = 0;
            while (u < n) {
                line = br.readLine();
                if (line == null || line.trim().length() == 0) {
                    logger.error("The data is incomplete!");
                    System.exit(1);
                }
                final String[] vArr = line.split("\\s+");
                if (vArr.length != n) {
                    logger.info(vArr.length);
                    logger.error("Bad format for data input stream!");
                    System.exit(1);
                }
                for (int v = 0; v < vArr.length; v++) {
                    final int value = Integer.parseInt(vArr[v]);
                    // value > 0 or value = 0
                    if (value > 0) {
                        graph.insert(new Edge(u, v));
                    }
                }
                u++;
            }
            br.close();
        } catch (final IOException e) {
            logger.warn(e);
        }
        return graph;
    }

    /**
     * Scans a graph from a text data file name. The data file is organised as
     * follows:
     * <ul>
     * <li>The first line is the number of vertices of the graph</li>
     * <li>The other lines contains a matrix representing the graph.
     * </ul>
     *
     * @param filename
     * @see #scanAdjacencyMatrix(Reader)
     * @return an adjacency list graph.
     */
    public static IGraph scanAdjacencyMatrix(final String filename) {
        IGraph graph = null;
        try {
            final InputStreamReader isr = new FileReader(filename);
            graph = scanAdjacencyMatrix(isr);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        }
        return graph;
    }

    /**
     * Print to the standard output all edges of the graph
     *
     * @param graph
     */
    public static void printEdges(final IGraph graph) {
        Edge[] edges = null;
        if (graph instanceof IWeightedGraph) {
            edges = GraphUtilities.getWeightedEdges((IWeightedGraph) graph);
        } else {
            edges = GraphUtilities.getEdges(graph);
        }
        for (final Edge e : edges) {
            final double w = e.getWeight();
            logger.info(e.getU() + " - " + e.getV() + " (" + w + ")");
        }
        // logger.info("There are {} edges.", edges.length);
    }

    /**
     * Print out a sparse graph to the standard output.
     *
     * @param graph
     *            a sparse graph
     */
    private static void printSparseGraph(final IGraph graph) {
        printEdges(graph);
    }

    /**
     * Print out a dense graph to the standard output.
     *
     * @param graph
     */
    private static void printDenseGraph(final IGraph graph) {
        final int n = graph.getNumberOfVertices();
        logger.info("\t");
        for (int u = 0; u < n; u++) {
            logger.info("\t{}", u);
        }
        logger.info("\n");
        logger.info("\t");
        for (int u = 0; u < n; u++) {
            logger.info("\t-");
        }
        logger.info("\n");
        for (int u = 0; u < n; u++) {
            logger.info(u + "\t" + "|");
            for (int v = 0; v < n; v++) {
                final int b = (graph.edge(u, v) ? 1 : 0);
                logger.info("\t{}", b);
            }
            logger.info("\n");
        }
    }

    /**
     * Print out a graph.
     *
     * @param graph
     */
    public static void print(final IGraph graph) {
        final int vC = graph.getNumberOfVertices();
        final int eC = graph.getNumberOfEdges();
        logger.info("There are {} vertices and {} edges.\n", vC, eC);
        if (graph instanceof AdjacencyListGraph || graph instanceof AdjacencyListWeightedGraph) {
            printSparseGraph(graph);
        } else {
            if (graph instanceof AdjacencyMatrixGraph) {
                printDenseGraph(graph);
            }
        }

        logger.info("\n");
    }

    /**
     * Scan a weighted graph from an input stream reader. This method is usually
     * invoked by the method {@link #scanAdjacencyListWeighted(String)}.
     *
     * @see #scanAdjacencyListWeighted(String)
     * @return an adjacency list weighted graph.
     */
    public static IWeightedGraph scanAdjacencyListWeighted(final InputStreamReader inputStreamReader) {
        IWeightedGraph graph = null;
        final BufferedReader br = new BufferedReader(inputStreamReader);
        try {
            // read the number of vertices of the graph
            // that is specified on the first line.
            final int n = Integer.parseInt(br.readLine());
            // logger.info(n);
            if (n > 0) {
                // create a graph with n vertices
                graph = new AdjacencyListWeightedGraph(n, DIRECTED);
            } else {
                logger.error("The number of vertices of the graph must > 0.");
                System.exit(1);
            }
            // read edges of the graph
            String line = "";
            while ((line = br.readLine()) != null && line.trim().length() > 0) {
                final String[] uvw = line.split("\\s+");
                if (uvw.length != 3) {
                    logger.error("Bad format for data input stream!");
                    System.exit(1);
                }
                final int u = Integer.parseInt(uvw[0]);
                final int v = Integer.parseInt(uvw[1]);
                final double weight = Double.parseDouble(uvw[2]);
                // insert a new edge to the graph
                graph.insert(new Edge(u, v, weight));
            }
            br.close();
        } catch (final IOException e) {
            logger.warn(e);
        }
        return graph;
    }

    /**
     * Scan a graph from a text data file name. The data file is organised as
     * follows:
     * <ul>
     * <li>The first line is the number of vertices of the graph</li>
     * <li>The other lines contains triples u v w (separated by at least a blank char)
     * that represent edges (u,v) of the graph and its weight (w).</li>
     * </ul>
     * See the samples directory for sample weighted graphs.
     *
     * @param filename
     * @return an adjacency list weighted graph.
     */
    public static IWeightedGraph scanAdjacencyListWeighted(final String filename) {
        IWeightedGraph graph = null;
        try {
            final InputStreamReader isr = new FileReader(filename);
            graph = scanAdjacencyListWeighted(isr);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        }
        return graph;
    }

}
