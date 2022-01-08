import java.io.File;
import java.io.IOException;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File(args[0]));
            int countOfOp = Integer.parseInt(scanner.nextLine());
            String temp = scanner.next();
            BTree root = new BTree(Integer.parseInt(temp));
            BTree.first = root;

            while (scanner.hasNext()) {
                temp = scanner.next();
                root = root.addElementSTART(new BTree(Integer.parseInt(temp)));
            }

            for (int i = 0; i < countOfOp; i++) {
                root = root.index(root, BTree.pointer);

                if (root.value % 2 == 0) { //delete
                    boolean operationOnFirst = false;

                    if (root == BTree.first) operationOnFirst = true;
                    if (BTree.size == 1) {
                        BTree.size--;
                        root = null;
                        break;
                    }

                    if (BTree.pointer + 1 == BTree.size) {
                        BTree.size--;
                        root = BTree.first;
                        BTree.pointer = (root.value - 1) % (BTree.size);

                        BTree tmp2 = root.par;

                        if (root.hasRigth()) {

                            if (BTree.size < 3 && tmp2 == null) {
                                root.value = root.rigth.value;
                                root.rigth = null;
                                continue;
                            }

                            if (tmp2.left == root) tmp2.left = root.rigth;
                            else tmp2.rigth = root.rigth;

                            root.rigth.par = tmp2;
                            root.par = null;
                            root.rigth = null;
                            root = tmp2;

                            while (root.left != null)
                                root = root.left;

                            BTree.first = root;
                            root = root.par;

                            root = root.up(false, false, false, 0);
                            continue;
                        } else {
                            if (tmp2.left == root) {
                                tmp2.left = null;
                                BTree.first = tmp2;
                                root = root.up(false, false, false, 0);
                                continue;
                            } else {
                                tmp2.rigth = null;
                                root = root.up(false, true, false, 0);
                                continue;
                            }
                        }
                    }
                    BTree.size--;
                    BTree.pointer = (BTree.pointer + root.value) % (BTree.size);

                    if (root.hasRigth()) {
                        root = root.rigth;

                        while (root.left != null)
                            root = root.left;

                        if (root.hasRigth()) {
                            if (root.par.left == root) root.par.left = root.rigth;
                            else root.par.rigth = root.rigth;

                            BTree tmp2 = root;
                            root = root.rigth;
                            root.par = tmp2.par;
                            tmp2.par = null;
                            tmp2.rigth = null;

                        } else {
                            BTree tmp2 = root.par;

                            if (root.par.left == root) root.par.left = null;
                            else root.par.rigth = null;

                            root.par = null;
                            root = tmp2;
                        }
                        root = root.up(false, false, false, 0);

                    } else {
                        int tmpVal = root.value;
                        BTree tmp2 = root.left;

                        if (tmp2 != null) {
                            tmp2.par = root.par;

                            if (root.par.left == root) root.par.left = tmp2;
                            else root.par.rigth = tmp2;

                            root.par = null;
                            root.left = null;
                            root = tmp2;

                            root = root.up(true, false, operationOnFirst, tmpVal);
                        } else {
                            tmp2 = root.par;
                            root.par = null;

                            if (tmp2.left == root) {
                                tmp2.left = null;
                                root = tmp2;
                                tmp2.value = tmpVal;
                                if (operationOnFirst) BTree.first = root;
                                root = root.up(false, false, false, tmpVal);
                            } else {
                                tmp2.rigth = null;
                                root = tmp2;
                                root = root.up(true, false, operationOnFirst, tmpVal);
                            }
                        }
                    }
                } else { //ADD
                    BTree av = new BTree(root.value - 1);
                    BTree.pointer = (BTree.pointer + root.value) % (BTree.size);
                    if (!root.hasRigth()) {
                        root.rigth = av;
                    } else {
                        root = root.rigth;
                        while (root.left != null)
                            root = root.left;

                        root.left = av;
                    }
                    av.par = root;
                    root = root.up(false, false, false, 0);
                }
            }

            if (BTree.size != 0)
                for (int i = 0; i < BTree.size; i++) {
                    int move = BTree.pointer + i;
                    if (move > BTree.size - 1) move = move - BTree.size;

                    System.out.print(i + 1 != BTree.size ? root.index(root, move).value + " " : root.index(root, move).value);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class BTree {
    int value;
    int lh = 0;
    int rh = 0;
    int h = 0;
    int lsumBelow = 0;
    int sumBelow = 0;
    BTree left;
    BTree rigth;
    BTree par;
    static BTree first;
    static int size = 0;
    static int pointer = 0;

    public BTree(int value) {
        this.value = value;
        size++;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRigth() {
        return rigth != null;
    }

    public BTree up(boolean del, boolean newmin, boolean frst, int val) {
        BTree point = this;

        while (point != null) {
            if (del && point.par != null && point.par.left == point) {
                BTree.pointer = (BTree.pointer + point.par.value) % (BTree.size);
                point.par.value = val;
                if (frst) BTree.first = point.par;
                del = false;
            }

            if (newmin && point.par != null && point.par.left == point) {
                BTree.first = point.par;
                newmin = false;
            }

            point.sumBelow = 0;
            if (point.hasRigth()) {
                point.rh = point.rigth.h + 1;
                point.sumBelow += point.rigth.sumBelow + 1;
            } else point.rh = 0;
            if (point.hasLeft()) {
                point.lh = point.left.h + 1;
                point.sumBelow += point.left.sumBelow + 1;
                point.lsumBelow = point.left.sumBelow + 1;
            } else {
                point.lh = 0;
                point.lsumBelow = 0;
            }
            point.h = point.rh > point.lh ? point.rh : point.lh;


            if (point.lh - point.rh > 1) {
                int bal = 0;
                if (point.hasLeft() && point.hasRigth() && point.lh > point.rh)
                    bal = point.left.lh - point.left.rh;
                if (point.hasLeft() && point.hasRigth() && point.lh < point.rh)
                    bal = point.rigth.lh - point.rigth.rh;
                if (!point.hasLeft() && point.hasRigth()) bal = point.rigth.lh - point.rigth.rh;
                if (point.hasLeft() && !point.hasRigth()) bal = point.left.lh - point.left.rh;

                if (bal >= 0) point = point.rotRight();
                else point = point.rotRight2();
            }

            if (point.lh - point.rh < -1) {
                int bal = 0;
                if (point.hasLeft() && point.hasRigth() && point.lh > point.rh)
                    bal = point.left.lh - point.left.rh;
                if (point.hasLeft() && point.hasRigth() && point.lh < point.rh)
                    bal = point.rigth.lh - point.rigth.rh;
                if (!point.hasLeft() && point.hasRigth()) bal = point.rigth.lh - point.rigth.rh;
                if (point.hasLeft() && !point.hasRigth()) bal = point.left.lh - point.left.rh;

                if (bal < 0) point = point.rotLeft();
                else point = point.rotLeft2();

            }

            if (point.par == null) return point;
            point = point.par;
        }
        return null;
    }


    public BTree addElementSTART(BTree tree) {
        BTree n = this;

        while (n.hasRigth())
            n = n.rigth;

        n.rigth = tree;
        tree.par = n;

        while (n != null) {
            n.sumBelow = 0;
            if (n.hasRigth()) {
                n.rh = n.rigth.h + 1;
                n.sumBelow += n.rigth.sumBelow + 1;
            } else n.rh = 0;
            if (n.hasLeft()) {
                n.lh = n.left.h + 1;
                n.sumBelow += n.left.sumBelow + 1;
                n.lsumBelow = n.left.sumBelow + 1;
            } else {
                n.lh = 0;
                n.lsumBelow = 0;
            }
            n.h = n.rh > n.lh ? n.rh : n.lh;

            if (n.lh - n.rh > 1) {
                int bal = 0;
                if (n.hasLeft() && n.hasRigth() && n.lh > n.rh)
                    bal = n.left.lh - n.left.rh;
                if (n.hasLeft() && n.hasRigth() && n.lh < n.rh)
                    bal = n.rigth.lh - n.rigth.rh;
                if (!n.hasLeft() && n.hasRigth()) bal = n.rigth.lh - n.rigth.rh;
                if (n.hasLeft() && !n.hasRigth()) bal = n.left.lh - n.left.rh;

                if (bal >= 0) n = n.rotRight();
                else n = n.rotRight2();
            }

            if (n.lh - n.rh < -1) {
                int bal = 0;
                if (n.hasLeft() && n.hasRigth() && n.lh > n.rh)
                    bal = n.left.lh - n.left.rh;
                if (n.hasLeft() && n.hasRigth() && n.lh < n.rh)
                    bal = n.rigth.lh - n.rigth.rh;
                if (!n.hasLeft() && n.hasRigth()) bal = n.rigth.lh - n.rigth.rh;
                if (n.hasLeft() && !n.hasRigth()) bal = n.left.lh - n.left.rh;

                if (bal < 0) n = n.rotLeft();
                else n = n.rotLeft2();
            }

            if (n.par == null) return n;
            n = n.par;
        }
        return null;
    }

    public BTree rotRight() {
        BTree cur = this;
        BTree l1 = cur.left;
        BTree l2 = l1.left;
        BTree r1 = l1.rigth;
        BTree r2 = cur.rigth;
        BTree tmp = l1;

        tmp.rigth = cur;
        tmp.rigth.rigth = r2;
        tmp.rigth.left = r1;
        tmp.left = l2;

        cur.sumBelow = 0;
        if (r1 != null) {
            r1.par = cur;
            cur.lh = r1.h + 1;
            cur.sumBelow += r1.sumBelow + 1;
            cur.lsumBelow = r1.sumBelow + 1;
        } else {
            cur.lh = 0;
            cur.lsumBelow = 0;
        }
        if (r2 != null) {
            r2.par = cur;
            cur.rh = r2.h + 1;
            cur.sumBelow += r2.sumBelow + 1;
        } else cur.rh = 0;
        cur.h = cur.rh > cur.lh ? cur.rh : cur.lh;

        l1.sumBelow = cur.sumBelow + 1;
        if (l2 != null) {
            l2.par = tmp;
            tmp.lh = l2.h + 1;
            l1.sumBelow += l2.sumBelow + 1;
            l1.lsumBelow = l2.sumBelow + 1;
        } else {
            tmp.lh = 0;
            l1.lsumBelow = 0;
        }
        tmp.rh = cur.h + 1;
        tmp.h = tmp.rh > tmp.lh ? tmp.rh : tmp.lh;

        if (cur.par != null) {
            if (cur.par.rigth == cur) cur.par.rigth = tmp;
            else cur.par.left = tmp;
        }

        tmp.par = cur.par;
        cur.par = tmp;

        return tmp;
    }

    public BTree rotLeft() {
        BTree cur = this;
        BTree l1 = cur.rigth;
        BTree l2 = l1.rigth;
        BTree r1 = l1.left;
        BTree r2 = cur.left;
        BTree tmp = l1;

        tmp.left = cur;
        tmp.left.left = r2;
        tmp.left.rigth = r1;
        tmp.rigth = l2;

        cur.sumBelow = 0;
        if (r1 != null) {
            r1.par = cur;
            cur.rh = r1.h + 1;
            cur.sumBelow += r1.sumBelow + 1;
        } else cur.rh = 0;
        if (r2 != null) {
            r2.par = cur;
            cur.lh = r2.h + 1;
            cur.sumBelow += r2.sumBelow + 1;
            cur.lsumBelow = r2.sumBelow + 1;
        } else {
            cur.lh = 0;
            cur.lsumBelow = 0;
        }
        cur.h = cur.rh > cur.lh ? cur.rh : cur.lh;

        l1.sumBelow = cur.sumBelow + 1;
        l1.lsumBelow = cur.sumBelow + 1;
        if (l2 != null) {
            l2.par = tmp;
            tmp.rh = l2.h + 1;
            l1.sumBelow += l2.sumBelow + 1;
        } else tmp.rh = 0;
        tmp.lh = cur.h + 1;
        tmp.h = tmp.rh > tmp.lh ? tmp.rh : tmp.lh;


        if (cur.par != null) {
            if (cur.par.rigth == cur) cur.par.rigth = tmp;
            else cur.par.left = tmp;
        }

        tmp.par = cur.par;
        cur.par = tmp;

        return tmp;
    }

    public BTree rotRight2() {
        BTree n = this.left.rotLeft();
        n = n.par.rotRight();
        return n;
    }

    public BTree rotLeft2() {
        BTree n = this.rigth.rotRight();
        n = n.par.rotLeft();

        return n;
    }

    public static BTree index(BTree n, int index) {
        if (index == n.lsumBelow) return n;
        if (index < n.lsumBelow) return index(n.left, index);
        if (index > n.lsumBelow) return index(n.rigth, index - n.lsumBelow - 1);
        return null;
    }
}
