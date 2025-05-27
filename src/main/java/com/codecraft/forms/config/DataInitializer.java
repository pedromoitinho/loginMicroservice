package com.codecraft.forms.config;

import com.codecraft.forms.entity.Form;
import com.codecraft.forms.entity.FormQuestion;
import com.codecraft.forms.entity.FormQuestion.QuestionType;
import com.codecraft.forms.repository.FormRepository;
import com.codecraft.forms.repository.FormQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FormQuestionRepository formQuestionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem formulários
        if (formRepository.count() > 0) {
            return; // Dados já foram inicializados
        }

        // Criar formulário de exemplo
        Form form = new Form();
        form.setTitle("Avaliação de Satisfação de Clientes");
        form.setDescription("Formulário para avaliar a satisfação dos nossos clientes com nossos serviços");
        form.setCreatedAt(LocalDateTime.now());
        form.setUpdatedAt(LocalDateTime.now());
        form.setIsActive(true);

        Form savedForm = formRepository.save(form);

        // Criar perguntas do formulário
        List<FormQuestion> questions = Arrays.asList(
            createQuestion(savedForm, "Como você avalia nosso atendimento?", QuestionType.RATING, 1, true, null),
            createQuestion(savedForm, "Qual serviço você utilizou?", QuestionType.SINGLE_CHOICE, 2, true, 
                Arrays.asList("Consultoria em Segurança", "Treinamento NR-10", "Assessoria em RH", "Psicologia Organizacional", "Saúde Ocupacional")),
            createQuestion(savedForm, "Recomendaria nossos serviços para outras empresas?", QuestionType.SINGLE_CHOICE, 3, true,
                Arrays.asList("Sim, definitivamente", "Provavelmente sim", "Talvez", "Provavelmente não", "Não")),
            createQuestion(savedForm, "Deixe um comentário sobre sua experiência:", QuestionType.TEXTAREA, 4, false, null),
            createQuestion(savedForm, "Qual sua empresa?", QuestionType.TEXT, 5, true, null),
            createQuestion(savedForm, "Seu email para contato:", QuestionType.EMAIL, 6, false, null)
        );

        formQuestionRepository.saveAll(questions);

        System.out.println("✅ Formulário de exemplo criado com sucesso!");
    }

    private FormQuestion createQuestion(Form form, String questionText, QuestionType type, int order, 
                                      boolean required, List<String> options) {
        FormQuestion question = new FormQuestion();
        question.setForm(form);
        question.setQuestionText(questionText);
        question.setType(type);
        question.setOrder(order);
        question.setIsRequired(required);
        
        if (options != null && !options.isEmpty()) {
            // Convert List<String> to JSON string
            StringBuilder optionsJson = new StringBuilder();
            optionsJson.append("[");
            for (int i = 0; i < options.size(); i++) {
                optionsJson.append("\"").append(options.get(i)).append("\"");
                if (i < options.size() - 1) {
                    optionsJson.append(",");
                }
            }
            optionsJson.append("]");
            question.setOptions(optionsJson.toString());
        }
        
        return question;
    }
}
