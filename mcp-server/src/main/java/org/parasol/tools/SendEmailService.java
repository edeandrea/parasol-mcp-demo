package org.parasol.tools;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.quarkiverse.mcp.server.ToolResponse;

import dev.langchain4j.agent.tool.P;

@ApplicationScoped
public class SendEmailService {
  public record Email(
      @P("The to address") String to,
      @P("The from address") String from,
      @P("The email subject") String subject,
      @P("The email body") String body) {}

  private final Mailer mailer;
  private final Tracer tracer;

  public SendEmailService(Mailer mailer, Tracer tracer) {
    this.mailer = mailer;
    this.tracer = tracer;
  }

//  @dev.langchain4j.agent.tool.Tool(name = "sendEmail", value = "Sends an email")
  public ToolResponse sendEmail(Email email) {
    var span = this.tracer.spanBuilder("sendEmail")
        .setAttribute("arg.email", email.toString())
        .setParent(Context.current().with(Span.current()))
        .setSpanKind(SpanKind.SERVER)
        .startSpan();

    try {
      var mail = Mail.withText(email.to(), email.subject(), email.body())
          .setFrom(email.from());

      this.mailer.send(mail);

      return ToolResponse.success("Email successfully sent");
    }
    finally {
      span.end();
    }
  }
}
