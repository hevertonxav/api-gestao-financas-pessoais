package com.heverton.api_gestao_financas_pessoais.controllers.handlers;

import com.heverton.api_gestao_financas_pessoais.dtos.responses.ValidacaoErro;
import com.heverton.api_gestao_financas_pessoais.exceptions.CategoriaJaExistenteException;
import com.heverton.api_gestao_financas_pessoais.exceptions.DataBaseException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RegraNegocioException;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.List;


@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(CategoriaJaExistenteException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail conflict(final Throwable exception) {

        final var exceptionMessage = exception.getMessage();

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exceptionMessage);

        problemDetail.setTitle("Conflict");
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/recurso-ja-existe"));

        return problemDetail;
    }

    @ExceptionHandler(DataBaseException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail badRequest(final Throwable exception) {

        final var exceptionMessage = exception.getMessage();

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exceptionMessage);

        problemDetail.setTitle("Transação vinculada");
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/transacao-vinculada"));

        return problemDetail;
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail recursoNaoEncontrado(final Throwable exception) {

        final var exceptionMessage = exception.getMessage();

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exceptionMessage);

        problemDetail.setTitle("recurso não encontrado");
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/recurso-nao-encontrado"));

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(final MethodArgumentNotValidException exception) {

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Dados inválidos");
        problemDetail.setDetail("Um ou mais campos são inválidos.");
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/dados-invalidos"));

        List<ValidacaoErro> erros = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> new ValidacaoErro(
                        erro.getField(),
                        erro.getDefaultMessage()))
                .toList();

        problemDetail.setProperty("erros", erros);

        return problemDetail;
    }

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail regraDeNegocio(final Throwable exception) {

        final var exceptionMessage = exception.getMessage();

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exceptionMessage);

        problemDetail.setTitle("Regra de negócio");
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/regra-de-negocio"));

        return problemDetail;
    }

    @ExceptionHandler(BeanInstantiationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail beanInstantiation(BeanInstantiationException exception) {

        Throwable causa = exception.getCause();

        if (causa instanceof RegraNegocioException regraNegocioException) {

            var problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    regraNegocioException.getMessage()
            );

            problemDetail.setTitle("Regra de negócio");
            problemDetail.setType(
                    URI.create("https://api.financas.com.br/erros/regra-de-negocio")
            );

            return problemDetail;
        }

        throw exception;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(
                HttpStatus.BAD_REQUEST
        );

        problemDetail.setTitle("Data inválida");
        problemDetail.setDetail(
                "A data deve estar no formato yyyy-MM-dd e ser uma data válida."
        );
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/formato-data-invalido"));

        return problemDetail;
    }



    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(
            NoResourceFoundException ex, HttpRequestMethodNotSupportedException ex1) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(
                HttpStatus.NOT_FOUND
        );

        problemDetail.setTitle("Not Found");
        problemDetail.setDetail(
                "Requisição digitada incorretamente"
        );
        problemDetail.setType(URI.create("https://api.financas.com.br/erros/requisicao-digitada-incorretamente"));

        return problemDetail;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(
                HttpStatus.METHOD_NOT_ALLOWED
        );

        problemDetail.setTitle("Method Not Allowed");
        problemDetail.setDetail(
                "Método HTTP não permitido para esta requisição."
        );

        problemDetail.setType(URI.create("https://api.financas.com.br/erros/metodo-http-nao-permitido"));

        return problemDetail;
    }

}
