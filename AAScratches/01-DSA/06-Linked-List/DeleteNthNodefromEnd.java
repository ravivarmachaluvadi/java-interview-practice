class DeleteNthNodefromEnd {
    // Function to print the linked list
    public static void printLL(Node head) {
        while (head != null) {
            System.out.print(head.data + " ");
            head = head.next;
        }
    }

    public static Node deleteNthNodefromEnd(Node head, int N) {
        // Create two pointers, fastp and slowp
        Node fastp = head;
        Node slowp = head;

        // Move the fastp pointer N nodes ahead
        // if i=1 then up to <=N
        for (int i = 0; i < N; i++) fastp = fastp.next;

        // If fastp becomes null, the Nth node from the end is the head
        if (fastp == null) return head.next;

        // Move both pointers until fastp reaches the end
        // remember fastp.next != null in remove nth node from end
        while (fastp.next != null) {
            fastp = fastp.next;
            slowp = slowp.next;
        }

        slowp.next = slowp.next.next;
        return head;
    }

    // 1 2 4 5
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        int N = 3;
        Node head = new Node(arr[0]);
        head.next = new Node(arr[1]);
        head.next.next = new Node(arr[2]);
        head.next.next.next = new Node(arr[3]);
        head.next.next.next.next = new Node(arr[4]);

        head = deleteNthNodefromEnd(head, N);
        printLL(head);
    }
}

class Node {
    public int data;
    public Node next;

    public Node(int data1, Node next1) {
        data = data1;
        next = next1;
    }

    // Constructor for Node with only data (next set to null)
    public Node(int data1) {
        data = data1;
        next = null;
    }
}
