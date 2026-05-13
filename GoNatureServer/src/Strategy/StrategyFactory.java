package Strategy;

public class StrategyFactory {

    public static MessageStrategy getStrategy(String command) {

        switch (command) {
        
            case "GET_ORDERS":
                return new GetOrdersStrategy();

            case "UPDATE_ORDER":
                return new UpdateOrderStrategy();

            default:
                return null;
        }
    }
}