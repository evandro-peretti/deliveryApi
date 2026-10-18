package com.br.deliveryApi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

/**
 * Todas as respostas de erro seguem o formato RFC 9457 (application/problem+json):
 * type, title, status, detail, instance + timestamp (ISO UTC).
 * Erros de validação trazem ainda a lista "errors" com campo e mensagem.
 *
 * Herdar de ResponseEntityExceptionHandler cobre ResponseStatusException (404, 409, ...)
 * e os erros padrão do Spring MVC (método não permitido, rota inexistente, etc.).
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Bean Validation (@Valid no corpo da requisição). */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<CampoInvalido> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new CampoInvalido(fe.getField(),
                        fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "valor inválido"))
                .toList();

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status,
                "Um ou mais campos da requisição são inválidos");
        problema.setTitle("Dados inválidos");
        problema.setProperty("errors", erros);
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    /** JSON malformado, corpo ausente ou valor de enum inexistente (ex.: status "VOANDO"). */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status,
                "Corpo da requisição ausente, malformado ou com valor inválido");
        problema.setTitle("Requisição inválida");
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    /** Parâmetro de rota/consulta com tipo errado (ex.: /pedidos/abc). */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatusCode status,
                                                        WebRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status,
                "Valor inválido para o parâmetro '" + ex.getPropertyName() + "'");
        problema.setTitle("Parâmetro inválido");
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    /** Violação de constraint do banco (ex.: email duplicado numa corrida entre requisições). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleConflitoDeDados(DataIntegrityViolationException ex, WebRequest request) {
        log.warn("Violação de integridade de dados", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "A operação viola uma restrição de integridade dos dados");
        problema.setTitle("Conflito de dados");
        return handleExceptionInternal(ex, problema, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /** Rede de segurança: nunca vaza stack trace nem mensagem interna para o cliente. */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleErroInesperado(Exception ex, WebRequest request) {
        log.error("Erro inesperado", ex);
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
        problema.setTitle("Erro interno");
        return handleExceptionInternal(ex, problema, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /** Ponto único por onde passam todas as respostas: acrescenta o timestamp. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> resposta = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (resposta != null && resposta.getBody() instanceof ProblemDetail problema) {
            problema.setProperty("timestamp", Instant.now());
        }
        return resposta;
    }
}
