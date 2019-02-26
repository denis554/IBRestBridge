package IBRest.Controller;

import IBRest.Constants.IBRestURIConstants;
import IBRest.Model.RestAPI.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RestAPIController {

    @RequestMapping(value = IBRestURIConstants.MARKET, method = RequestMethod.GET)
    public Market Market(@PathVariable("a") String action,
                         @PathVariable("q") double quantity,
                         @PathVariable("st") String stock,
                         @PathVariable("s") String symbol,
                         @PathVariable("c") String currencyUnit,
                         @PathVariable("e") String exchange,
                         HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [Market] API From " + ipAddress +
                ", [Action] = " + action +
                ", [Quantity] = " + quantity +
                ", [Stock] = " + stock +
                ", [Symbol] = " + symbol +
                ", [Currency] = " + currencyUnit +
                ", [Exchange] = " + exchange +
                ", [Path] = " + request.getRequestURI());

        return new Market(action, quantity, stock, symbol, currencyUnit, exchange, request);
    }

    @RequestMapping(value = IBRestURIConstants.LIMIT_ORDER, method = RequestMethod.GET)
    public LimitOrder LimitOrder(@PathVariable("a") String action,
                                 @PathVariable("p") double price,
                                 @PathVariable("q") double quantity,
                                 @PathVariable("st") String stock,
                                 @PathVariable("s") String symbol,
                                 @PathVariable("c") String currencyUnit,
                                 @PathVariable("e") String exchange,
                                 HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [LimitOrder] API From " + ipAddress +
                ", [Action] = " + action +
                ", [Price] = " + price +
                ", [Quantity] = " + quantity +
                ", [Stock] = " + stock +
                ", [Symbol] = " + symbol +
                ", [Currency] = " + currencyUnit +
                ", [Exchange] = " + exchange +
                ", [Path] = " + request.getRequestURI());
        return new LimitOrder(action, price, quantity, stock, symbol, currencyUnit, exchange, request);
    }

    @RequestMapping(value = IBRestURIConstants.ORDER_STATUS_BY_ID, method = RequestMethod.GET)
    public OrderStatus OrderStatus(@PathVariable("id") int id,
                                   HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [OrderStatus] API From " + ipAddress +
                ", [Id] = " + id +
                ", [Path] = " + request.getRequestURI());
        return new OrderStatus(id, request);
    }

    @RequestMapping(value = IBRestURIConstants.CANCEL_ALL_ORDERS, method = RequestMethod.GET)
    public CancelAllOrders CancelAllOrders(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [CancelAllOrders] API From " + ipAddress +
                ", path = " + request.getRequestURI());
        return new CancelAllOrders(request);
    }

    @RequestMapping(value = IBRestURIConstants.BALANCE, method = RequestMethod.GET)
    public Balance Balance(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [Balance] API From " + ipAddress +
                ", path = " + request.getRequestURI());
        return new Balance(request);
    }

    @RequestMapping(value = IBRestURIConstants.REQ_MARKET_DATA, method = RequestMethod.GET)
    public ReqMarketData ReqMarketData(@PathVariable("st") String stock,
                                       @PathVariable("s") String symbol,
                                       @PathVariable("c") String currencyUnit,
                                       @PathVariable("e") String exchange,
                                       HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [ReqMarketData] API From " + ipAddress +
                ", [Stock] = " + stock +
                ", [Symbol] = " + symbol +
                ", [Currency] = " + currencyUnit +
                ", [Exchange] = " + exchange +
                ", [Path] = " + request.getRequestURI());
        return new ReqMarketData(stock, symbol, currencyUnit, exchange, request);
    }

    @RequestMapping(value = IBRestURIConstants.CANCEL_ORDER, method = RequestMethod.GET)
    public CancelOrder CancelOrder(@PathVariable("id") int id,
                                   HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [CancelOrder] API From " + ipAddress +
                ", [Id] = " + id +
                ", [Path] = " + request.getRequestURI());
        return new CancelOrder(id, request);
    }

    @RequestMapping(value = IBRestURIConstants.EXECUTION_ORDER, method = RequestMethod.GET)
    public ExecOrder ExecOrder(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [ExecOrder] API From " + ipAddress + ", path = " + request.getRequestURI());
        return new ExecOrder(request);
    }

    @RequestMapping(value = IBRestURIConstants.ORDER_EXEC_INFO, method = RequestMethod.GET)
    public ExecInfo Executions(@PathVariable("id") int id,
                               HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        System.out.println("Called [ExecInfo] API From " + ipAddress + ", path = " + request.getRequestURI());
        return new ExecInfo(id, request);
    }
}
