package ma.enset;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MasterAgent extends Agent {
    List<Individual> solutions=new ArrayList<>();
    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription=new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription=new ServiceDescription();
        serviceDescription.setName("master");
        serviceDescription.setType("ga");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this,dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        addBehaviour(new Behaviour() {
            int it=0;
            @Override
            public void action() {
                ACLMessage aclMessage=blockingReceive();
                String[] content=aclMessage.getContent().split("-");
                System.out.println("---------------------------------------------------");
                System.out.println("The best solution in island : " + aclMessage.getSender().getLocalName());
                System.out.println("Solution is : " + content[0] + " --> fitness : " + content[1]);

                solutions.add(new Individual(content[0].toCharArray(), Integer.valueOf(content[1]), aclMessage.getSender().getLocalName()));
                it++;
            }

            @Override
            public boolean done() {
                if(it == GAUtils.ISLAND_NUMBER) {
                    Collections.sort(solutions, Collections.reverseOrder());
                    char[] genes = solutions.get(0).getGenes();
                    String best = new String(genes);
                    System.out.println("*********************");
                    System.out.println("The optimal solution among all islands is determined on a per-island basis. : " + solutions.get(0).getAgentName());
                    System.out.println("Solution  : " + best + " --> fitness: " + solutions.get(0).getFitness());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}