import java.util.LinkedList;

/*** JUnit imports ***/
// We will use the BeforeEach and Test annotation types to mark methods in
// our test class.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// The Assertions class that we import from here includes assertion methods like assertEquals()
// which we will used in test1000Inserts().
import static org.junit.jupiter.api.Assertions.assertEquals;
// More details on each of the imported elements can be found here:
// https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/package-summary.html
/*** JUnit imports end  ***/

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Stack;

/**
 * Red-Black Tree implementation with a Node inner class for representing
 * the nodes of the tree. Currently, this implements a Binary Search Tree that
 * we will turn into a red black tree by modifying the insert functionality.
 * In this activity, we will start with implementing rotations for the binary
 * search tree insert algorithm. You can use this class' insert method to build
 * a regular binary search tree, and its toString method to display a level-order
 * traversal of the tree.
 */
public class RedBlackTree<T extends Comparable<T>> {

    /**
     * This class represents a node holding a single value within a binary tree
     * the parent, left, and right child references are always maintained.
     */
    protected static class Node<T> {
        public int blackHeight;
        public T data;
        public Node<T> parent; // null for root node
        public Node<T> leftChild;
        public Node<T> rightChild;
        public Node(T data) { this.data = data; this.blackHeight = 0;}
        /**
         * @return true when this node has a parent and is the left child of
         * that parent, otherwise return false
         */
        public boolean isLeftChild() {
            return parent != null && parent.leftChild == this;
        }

    }

    protected Node<T> root; // reference to root node of tree, null when empty
    protected int size = 0; // the number of values in the tree



    /**
     * Performs a naive insertion into a binary search tree: adding the input
     * data value to a new node in a leaf position within the tree. After
     * this insertion, no attempt is made to restructure or balance the tree.
     * This tree will not hold null references, nor duplicate data values.
     * @param data to be added into this binary search tree
     * @return true if the value was inserted, false if not
     * @throws NullPointerException when the provided data argument is null
     * @throws IllegalArgumentException when the newNode and subtree contain
     *      equal data references
     */
    public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
        // null references cannot be stored within this tree
        if(data == null) throw new NullPointerException(
                "This RedBlackTree cannot store null references.");

        Node<T> newNode = new Node<>(data);
        if(root == null) {
            root = newNode;
            size++;
            root.blackHeight = 1;
            return true;
        } // add first node to an empty tree
        else{
            boolean returnValue = insertHelper(newNode,root); // recursively insert into subtree
            if (returnValue) {size++; }
            else throw new IllegalArgumentException(
                    "This RedBlackTree already contains that value.");
            root.blackHeight = 1;
            return returnValue;
        }
    }

    /**
     * Recursive helper method to find the subtree with a null reference in the
     * position that the newNode should be inserted, and then extend this tree
     * by the newNode in that position.
     * @param newNode is the new node that is being added to this tree
     * @param subtree is the reference to a node within this tree which the
     *      newNode should be inserted as a descenedent beneath
     * @return true is the value was inserted in subtree, false if not
     */
    private boolean insertHelper(Node<T> newNode, Node<T> subtree) {
        int compare = newNode.data.compareTo(subtree.data);
        // do not allow duplicate values to be stored within this tree
        if (compare == 0)
            return false;

            // store newNode within left subtree of subtree
        else if (compare < 0) {
            if (subtree.leftChild == null) { // left subtree empty, add here
                subtree.leftChild = newNode;
                newNode.parent = subtree;
                enforceRBTreePropertiesAfterInsert(newNode);
                return true;
                // otherwise continue recursive search for location to insert
            } else
                return insertHelper(newNode, subtree.leftChild);
        }

        // store newNode within the right subtree of subtree
        else {
            if (subtree.rightChild == null) { // right subtree empty, add here
                subtree.rightChild = newNode;
                newNode.parent = subtree;
                enforceRBTreePropertiesAfterInsert(newNode);
                return true;
                // otherwise continue recursive search for location to insert
            } else
                return insertHelper(newNode, subtree.rightChild);
        }
    }

        /**
         * Resolve any red node with red parent property violations that are introduced by inserting new
         * nodes into a red-black tree. While doing so, all other red-black tree properties must also be
         * preserved.
         *
         * @param newNode inputNode
         */
        protected void enforceRBTreePropertiesAfterInsert(Node<T> newNode) {
        if (newNode == root) {
            root.blackHeight = 1;
            return;
        }
        // if newNode 's parent is root just return
        if (newNode.parent == root) {
            root.blackHeight = 1;
            return;
        }
        boolean testDirection;

        Node<T> grandParent;
        Node<T> uncle;

        while (newNode.parent.blackHeight == 0) {

            if (newNode.parent == root) {
                newNode.parent.blackHeight = 1;
                return;
            }

            testDirection = newNode.parent.isLeftChild();
            if (testDirection) {

                grandParent = newNode.parent.parent;
                uncle = newNode.parent.parent.rightChild;

                if (uncle != null && uncle.blackHeight == 0) {

                    newNode.parent.parent.blackHeight = 0;
                    grandParent = newNode.parent.parent;
                    newNode.parent.blackHeight = 1;
                    uncle.blackHeight = 1;
                    newNode = grandParent;
                }

                else {

                    if (newNode.isLeftChild()) {

                        newNode = newNode.parent;
                        rotate(newNode.leftChild, newNode);
                    }

                    newNode.parent.parent.blackHeight = 0;
                    newNode.parent.blackHeight = 1;
                    grandParent = newNode.parent.parent;
                    rotate(newNode.parent, newNode.parent.parent);
                }
            }
            else {

                grandParent = newNode.parent.parent;
                uncle = grandParent.leftChild;

                if (uncle != null && uncle.blackHeight == 0) {
                    newNode.parent.parent.blackHeight = 0;
                    grandParent = newNode.parent.parent;
                    newNode.parent.blackHeight = 1;
                    uncle.blackHeight = 1;
                    newNode = grandParent;
                }

                else {

                    if (newNode.isLeftChild()) {
                        newNode = newNode.parent;
                        rotate(newNode.leftChild, newNode);
                    }

                    newNode.parent.parent.blackHeight = 0;
                    grandParent = newNode.parent.parent;
                    newNode.parent.blackHeight = 1;
                    rotate(newNode.parent, newNode.parent.parent);
                }
            }

            if (newNode == root) {
                root.blackHeight = 1;
                return;
            }
            // if (newNode.parent.blackHeight == 0) {
            // enforceRBTreePropertiesAfterInsert(newNode);
            // }
        }
        // make sure root is always black
        root.blackHeight = 1;

    }


    /**
     * Performs the rotation operation on the provided nodes within this tree.
     * When the provided child is a leftChild of the provided parent, this
     * method will perform a right rotation. When the provided child is a
     * rightChild of the provided parent, this method will perform a left rotation.
     * When the provided nodes are not related in one of these ways, this method
     * will throw an IllegalArgumentException.
     * @param child is the node being rotated from child to parent position
     *      (between these two node arguments)
     * @param parent is the node being rotated from parent to child position
     *      (between these two node arguments)
     * @throws IllegalArgumentException when the provided child and parent
     *      node references are not initially (pre-rotation) related that way
     */
    private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
        // TODO: Implement this method.
        if (!child.parent.equals(parent))
            throw new IllegalArgumentException(
                    "the provided child and parent node references are not initially (pre-rotation)"
                            + "related that way");
        boolean test = false;
        if (child.parent == parent) {
            if (this.root == parent) {
                this.root = child;
                test = true;
            }
            if (child.isLeftChild()) {
                // left rotation
                Node<T> tempNode = child.rightChild;
                child.rightChild = parent;
                parent.leftChild = tempNode;
                child.parent = parent.parent;
                if (!test) {
                    if (parent.isLeftChild()) {
                        parent.parent.leftChild = child;
                    } else {
                        parent.parent.rightChild = child;
                    }
                }
                parent.parent = child;
                if (parent.leftChild != null) {
                    parent.leftChild.parent = parent;
                }
                return;
            } else if (!child.isLeftChild()) {
                // right rotation
                Node<T> tempNode = child.leftChild;
                child.parent = parent.parent;
                child.leftChild = parent;
                parent.rightChild = tempNode;
                if (!test) {
                    if (parent.isLeftChild()) {
                        parent.parent.leftChild = child;
                    } else {
                        parent.parent.rightChild = child;
                    }
                }
                parent.parent = child;
                if (parent.rightChild != null)
                    parent.rightChild.parent = parent;
            }
            return;
        }
    }


    /**
     * Get the size of the tree (its number of nodes).
     * @return the number of nodes in the tree
     */
    public int size() {
        return size;
    }

    /**
     * Method to check if the tree is empty (does not contain any node).
     * @return true of this.size() return 0, false if this.size() > 0
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Checks whether the tree contains the value *data*.
     * @param data the data value to test for
     * @return true if *data* is in the tree, false if it is not in the tree
     */
    public boolean contains(T data) {
        // null references will not be stored within this tree
        if(data == null) throw new NullPointerException(
                "This RedBlackTree cannot store null references.");
        return this.containsHelper(data, root);
    }

    /**
     * Recursive helper method that recurses through the tree and looks
     * for the value *data*.
     * @param data the data value to look for
     * @param subtree the subtree to search through
     * @return true of the value is in the subtree, false if not
     */
    private boolean containsHelper(T data, Node<T> subtree) {
        if (subtree == null) {
            // we are at a null child, value is not in tree
            return false;
        } else {
            int compare = data.compareTo(subtree.data);
            if (compare < 0) {
                // go left in the tree
                return containsHelper(data, subtree.leftChild);
            } else if (compare > 0) {
                // go right in the tree
                return containsHelper(data, subtree.rightChild);
            } else {
                // we found it :)
                return true;
            }
        }
    }


    /**
     * This method performs an inorder traversal of the tree. The string
     * representations of each data value within this tree are assembled into a
     * comma separated string within brackets (similar to many implementations
     * of java.util.Collection, like java.util.ArrayList, LinkedList, etc).
     * Note that this RedBlackTree class implementation of toString generates an
     * inorder traversal. The toString of the Node class class above
     * produces a level order traversal of the nodes / values of the tree.
     * @return string containing the ordered values of this tree (in-order traversal)
     */
    public String toInOrderString() {
        // generate a string of all values of the tree in (ordered) in-order
        // traversal sequence
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        sb.append(toInOrderStringHelper("", this.root));
        if (this.root != null) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(" ]");
        return sb.toString();
    }

    private String toInOrderStringHelper(String str, Node<T> node){
        if (node == null) {
            return str;
        }
        str = toInOrderStringHelper(str, node.leftChild);
        str += (node.data.toString() + ", ");
        str = toInOrderStringHelper(str, node.rightChild);
        return str;
    }

    /**
     * This method performs a level order traversal of the tree rooted
     * at the current node. The string representations of each data value
     * within this tree are assembled into a comma separated string within
     * brackets (similar to many implementations of java.util.Collection).
     * Note that the Node's implementation of toString generates a level
     * order traversal. The toString of the RedBlackTree class below
     * produces an inorder traversal of the nodes / values of the tree.
     * This method will be helpful as a helper for the debugging and testing
     * of your rotation implementation.
     * @return string containing the values of this tree in level order
     */
    public String toLevelOrderString() {
        String output = "[ ";
        if (this.root != null) {
            LinkedList<Node<T>> q = new LinkedList<>();
            q.add(this.root);
            while(!q.isEmpty()) {
                Node<T> next = q.removeFirst();
                if(next.leftChild != null) q.add(next.leftChild);
                if(next.rightChild != null) q.add(next.rightChild);
                output += next.data.toString();
                if(!q.isEmpty()) output += ", ";
            }
        }
        return output + " ]";
    }

    public String toString() {
        return "level order: " + this.toLevelOrderString() +
                "\nin order: " + this.toInOrderString();
    }


    // Implement at least 3 boolean test methods by using the method signatures below,
    // removing the comments around them and addind your testing code to them. You can
    // use your notes from lecture for ideas on concrete examples of rotation to test for.
    // Make sure to include rotations within and at the root of a tree in your test cases.
    // If you are adding additional tests, then name the method similar to the ones given below.
    // Eg: public static boolean test4() {}
    // Do not change the method name or return type of the existing tests.
    // You can run your tests by commenting in the calls to the test methods

//
//    public static boolean test1() {
//        try {
//            RedBlackTree<String> testTree = new RedBlackTree<String>();
//            Node<String> node4 = new Node<String>("4");//4
//            Node<String> node5 = new Node<String>("5");//5
//            Node<String> node6 = new Node<String>("6");//6
//            Node<String> node3 = new Node<String>("3");//3
//            Node<String> node2 = new Node<String>("2");//2
//            Node<String> node1 = new Node<String>("1");//1
//            Node<String> node7 = new Node<String>("7");//7
//            testTree.size = 7;
//            testTree.root = node4;
//            node4.leftChild = node5;
//            node4.rightChild = node6;
//            node5.parent = node4;
//            node5.leftChild = node3;
//            node5.rightChild = node2;
//            node6.parent = node4;
//            node6.leftChild = node1;
//            node6.rightChild = node7;
//            node3.parent = node5;
//            node2.parent = node5;
//            node1.parent = node6;
//            node7.parent = node6;
//            //System.out.println(testTree.toLevelOrderString());
//            testTree.rotate(node2, node5);
//            if (!testTree.toLevelOrderString().equals("[ 4, 2, 6, 5, 1, 7, 3 ]")) {
//                return false;
//            }
//            if (!testTree.toInOrderString().equals("[ 3, 5, 2, 4, 1, 6, 7 ]")) {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//
//    }
//
//
//
//    public static boolean test2() {
//        try {
//            RedBlackTree<String> testTree = new RedBlackTree<String>();
//            testTree.insert("3");
//            testTree.insert("2");
//            testTree.insert("1");
////            System.out.println(testTree.toString());
//            if (!testTree.toInOrderString().equals("[ 1, 2, 3 ]")) {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//
//
//    public static boolean test3() {
//        try {
//            RedBlackTree<String> tree = new RedBlackTree<String>();
//            tree.insert("29");
//            tree.insert("19");
//            tree.insert("99");
//            tree.insert("69");
//            tree.insert("39");
////            System.out.println(tree.toString());
//
//            if (!tree.toInOrderString().equals("[ 19, 29, 39, 69, 99 ]")) {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
    protected RedBlackTree<String> tree = null;
    @BeforeEach
    public void createInstane() {
        RedBlackTree<String> tree = new RedBlackTree<String>();
    }


    @Test
    public void test4() {

            Node<String> node7 = new Node<String>("7");
            Node<String> node14 = new Node<String>("14");
            Node<String> node18 = new Node<String>("18");
            Node<String> node23 = new Node<String>("23");
            tree.insert(node7.data);
            tree.insert(node14.data);
            tree.insert(node18.data);
            tree.insert(node23.data);
            assertEquals(1, tree.root.blackHeight);




    }

    @Test
    public void test5(){

        Node<String> node7 = new Node<String>("2");
        Node<String> node14 = new Node<String>("5");
        Node<String> node18 = new Node<String>("3");
        Node<String> node23 = new Node<String>("8");
        tree.insert(node7.data);
        tree.insert(node14.data);
        tree.insert(node18.data);
        tree.insert(node23.data);
        tree.insert("1");
        tree.insert("0");
        tree.insert("-3");
        tree.insert("-2");
        tree.insert("20");
        tree.insert("13");
        assertEquals(3, tree.root);
        assertEquals("[ -2, -3, 0, 1, 13, 2, 20, 3, 5, 8 ]", tree.toInOrderString());


    }

    @Test
    public void test6(){

        tree.insert("9");
        tree.insert("8");
        tree.insert("5");
        tree.insert("11");
        tree.insert("20");
        tree.insert("13");
        assertEquals(8, tree.root);
        assertEquals("[ 11, 13, 20, 5, 8, 9 ]", tree.toInOrderString());

    }




    /**
     * Main method to run tests. Comment out the lines for each test
     * to run them.
     * @param args
     */
    public static void main(String[] args) {
//         System.out.println("Test 1 passed: " + test1());
//         System.out.println("Test 2 passed: " + test2());
//         System.out.println("Test 3 passed: " + test3());
//        test5();
    }

}

