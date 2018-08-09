package txnapi.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DBRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:dbAggregate")
            .to("mongodb:mongo?database=blockchain&collection=transactions&operation=aggregate");
    }
}
