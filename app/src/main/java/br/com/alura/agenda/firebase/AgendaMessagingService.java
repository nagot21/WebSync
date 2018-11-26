package br.com.alura.agenda.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoSync;
import br.com.alura.agenda.event.AtualizarListaAlunoEvent;
import br.com.alura.agenda.sync.AlunoSincronizador;

/**
 * Created by IanNagot on 19/11/2018
 */
public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> mensagem = remoteMessage.getData();

        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem) {
        String chaveDeAcesso = "alunoSync";

        if (mensagem.containsKey(chaveDeAcesso)) {
            String json = mensagem.get(chaveDeAcesso);

            ObjectMapper mapper = new ObjectMapper();

            try {
                AlunoSync alunoSync = mapper.readValue(json, AlunoSync.class);

                new AlunoSincronizador(AgendaMessagingService.this).sincroniza(alunoSync);

                EventBus eventBus = EventBus.getDefault();

                eventBus.post(new AtualizarListaAlunoEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
