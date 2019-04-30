public class Node {
    Node leftChild;
    Node rightChild;
    Lettre lettre;

    public Node(Lettre lettre,Node left, Node right){
        this.leftChild = left;
        this.rightChild  = right;
        this.lettre = lettre;
    }
    
    public Lettre getLettre() {
        return lettre;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }
}
