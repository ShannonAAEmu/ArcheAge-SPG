import org.apache.commons.codec.binary.Hex;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final char[] xlBase64 = {'.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    private static String ip = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        System.out.println("Parameter for " + ip);
        generate();
        generateFromUserIp();
    }

    private static void generateFromUserIp() throws Exception {
        InetAddressValidator addressValidator = new InetAddressValidator();
        Scanner sc = new Scanner(System.in);
        boolean isValidIp = false;
        System.out.println("\nEnter your ip:");
        while (!isValidIp) {
            ip = sc.nextLine();
            isValidIp = addressValidator.isValidInet4Address(ip);
            if (!isValidIp)
                System.out.println("Enter valid ip:");
        }
        generate();
        System.out.println("\nPress \"Enter\" to exit.");
        sc.hasNextLine();
    }

    private static void generate() throws Exception {
        InetAddress ip = InetAddress.getByName(Main.ip);
        byte[] ipArray = ip.getAddress();
        byte[] paramArray = new byte[]{-43, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0};   // D5 ip1 04 ip2 ip4 ip3 rnd rnd 00 rnd rnd 00
        paramArray[1] = ipArray[0];
        paramArray[3] = ipArray[1];
        paramArray[4] = ipArray[3];
        paramArray[5] = ipArray[2];
        byte[] randomBytes = new byte[4];
        new Random().nextBytes(randomBytes);
        paramArray[6] = randomBytes[0];
        paramArray[7] = randomBytes[1];
        paramArray[9] = randomBytes[2];
        paramArray[10] = randomBytes[3];
        byte k = 0;
        for (int i = 0; i < paramArray.length; i++) {
            if (8 != i)
                k += paramArray[i];
        }
        k &= 0x3F;
        paramArray[8] = k;
        for (int i = 0; i < paramArray.length; i++) {
            if (10 != i)
                paramArray[i] ^= paramArray[10];
        }
        byte[] hexParamArray = Hex.decodeHex(Hex.encodeHexString(paramArray).toUpperCase());
        byte[] tempArray;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexParamArray.length; i += 3) {
            tempArray = new byte[]{hexParamArray[i + 2], hexParamArray[i + 1], hexParamArray[i]};
            int j = (int) (Long.parseLong(Hex.encodeHexString(tempArray).toUpperCase(), 16));
            sb.append(xlBase64[j & 0x3F]);
            j = j >> 6;
            sb.append(xlBase64[j & 0x3F]);
            j = j >> 6;
            sb.append(xlBase64[j & 0x3F]);
            j = j >> 6;
            sb.append(xlBase64[j & 0x3F]);
        }
        System.out.println(sb.toString());
    }

}
