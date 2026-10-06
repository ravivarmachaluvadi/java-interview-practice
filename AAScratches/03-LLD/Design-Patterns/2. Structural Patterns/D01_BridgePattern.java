/*
 * =====================================================================
 *  Bridge - notification type x delivery channel           Structural | Hard
 * =====================================================================
 *
 * PROBLEM
 *   The store sends OTPs, "order shipped" updates and promo offers, over
 *   SMS (160 chars, no subject), email and app push (short body). With one
 *   class per pair that is OtpSms, OtpEmail, ShippedPush... 9 classes, and
 *   adding WhatsApp means 3 more.
 *
 * KEY INSIGHT
 *   Two independent dimensions -> two hierarchies joined by a field.
 *   Notification decides WHAT is said; Channel decides HOW it travels. The
 *   `channel` field is the bridge: 3 + 3 classes cover all 9 pairs, and a
 *   new channel is written once (case 4). If you catch yourself naming a
 *   class OtpSmsNotification, you have two dimensions.
 *
 * ROLES IN THIS CODE
 *   Notification                     Abstraction (holds a Channel)
 *   OtpNotification, Shipped.., Promo..  RefinedAbstraction
 *   Channel                          Implementor
 *   SmsChannel, EmailChannel, Push.. ConcreteImplementor
 *
 * INTERVIEW FOLLOW-UPS
 *   - Bridge vs Strategy: same shape. Bridge is designed up front to let two
 *     hierarchies grow apart; Strategy swaps one algorithm in one class.
 *   - Seen in: JDBC (DriverManager API vs vendor Driver), SLF4J API vs
 *     Logback/Log4j binding.
 *
 * RUN
 *   4 cases: OTP by SMS, shipped by email, promo by push (cut short), a
 *   WhatsApp channel added as a lambda with no other changes.
 */

/** Implementor: HOW a message travels, with that channel's limits. */
interface Channel {
    String send(String to, String subject, String body);
}

class SmsChannel implements Channel {
    public String send(String to, String subject, String body) {
        return "SMS to " + to + ": " + cut(body, 160); // SMS has no subject line
    }

    static String cut(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}

class EmailChannel implements Channel {
    public String send(String to, String subject, String body) {
        return "EMAIL to " + to + " [" + subject + "] " + body;
    }
}

class PushChannel implements Channel {
    public String send(String to, String subject, String body) {
        return "PUSH to " + to + " [" + subject + "] " + SmsChannel.cut(body, 40);
    }
}

/** Abstraction: WHAT is said. The channel field is the bridge. */
abstract class Notification {
    protected final Channel channel;

    Notification(Channel channel) {
        this.channel = channel;
    }

    abstract String sendTo(String recipient);
}

class OtpNotification extends Notification {
    private final String otp;

    OtpNotification(Channel channel, String otp) {
        super(channel);
        this.otp = otp;
    }

    String sendTo(String recipient) {
        return channel.send(recipient, "Your OTP", otp + " is your login OTP, valid 5 minutes.");
    }
}

class ShippedNotification extends Notification {
    private final String orderId;
    private final String courier;

    ShippedNotification(Channel channel, String orderId, String courier) {
        super(channel);
        this.orderId = orderId;
        this.courier = courier;
    }

    String sendTo(String recipient) {
        return channel.send(recipient, "Order shipped",
                "Order " + orderId + " shipped via " + courier + ".");
    }
}

class PromoNotification extends Notification {
    private final String offer;

    PromoNotification(Channel channel, String offer) {
        super(channel);
        this.offer = offer;
    }

    String sendTo(String recipient) {
        return channel.send(recipient, "Sale is live", offer);
    }
}

class BridgePatternExample {

    public static void main(String[] args) {
        // Case 1-3: three message types over three channels, no OtpSms-style class.
        print("case 1 OTP sms     ",
                new OtpNotification(new SmsChannel(), "482913").sendTo("+910000000001"),
                "SMS to +910000000001: 482913 is your login OTP, valid 5 minutes.");
        print("case 2 shipped mail",
                new ShippedNotification(new EmailChannel(), "OD-1001", "Delhivery")
                        .sendTo("a@example.com"),
                "EMAIL to a@example.com [Order shipped] Order OD-1001 shipped via Delhivery.");

        // Case 3: edge. The push channel's length limit applies to ANY message type.
        print("case 3 promo push  ",
                new PromoNotification(new PushChannel(),
                        "Flat 50% off on sneakers. Ends Sunday midnight.").sendTo("device-7"),
                "PUSH to device-7 [Sale is live] Flat 50% off on sneakers. Ends Sunday...");

        // Case 4: a new channel, written once, works with every notification type.
        Channel whatsApp = (to, subject, body) -> "WHATSAPP to " + to + ": *" + subject + "* "
                + body;
        print("case 4 shipped wa  ",
                new ShippedNotification(whatsApp, "OD-1001", "BlueDart").sendTo("+910000000001"),
                "WHATSAPP to +910000000001: *Order shipped* Order OD-1001 shipped via BlueDart.");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
