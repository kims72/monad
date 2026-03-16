package spelbergit.list;

import java.util.Arrays;
import java.util.List;

public class ListStuff {

    public static void main(String[] args) {
        record Int(int value) {
            static Int of(int i) {
                return new Int(i);
            }
        }
        Int[] intsArr = new Int[]{Int.of(1), Int.of(2), Int.of(3), Int.of(4)};
        List<Int> ints = Arrays.asList(intsArr);

        for (Int i : ints) {

            ints.removeIf((Int t) -> t.value % 2 == 0);
            System.out.println("removed = " + i);
        }

    }
}
