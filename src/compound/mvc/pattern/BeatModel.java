package compound.mvc.pattern;

import java.util.ArrayList;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class BeatModel implements BeatModelInterface, MetaEventListener{
	Sequencer sequencer;
	ArrayList beatObservers=new ArrayList();
	ArrayList bpmObservers=new ArrayList();
	int bmp=90;
	Sequence sequence;
	Track track;
	
	@Override
	public void meta(MetaMessage message) {
		// TODO Auto-generated method stub
		if(message.getType()==47){
			beatEvent();
			sequencer.start();
			setBPM(getBPM());
		}
	}
	
	public void setUpMidi(){
		try{
			sequencer=MidiSystem.getSequencer();
			sequencer.open();
			sequencer.addMetaEventListener(this);
			sequence=new Sequence(Sequence.PPQ,4);
			track=sequence.createTrack();
			sequencer.setTempoInBPM(getBPM());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void buildTrackAndStart(){
		int[] trackList={35,0,46,0};
		sequence.deleteTrack(null);
		track=sequence.createTrack();
		
		makeTracks(trackList);
		track.add(makeEvent(192,9,1,0,4));
		try{
			sequencer.setSequence(sequence);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void makeTracks(int[] list){
		for(int i=0;i<list.length;i++){
			int key=list[i];
			if (key!=0){
				track.add(makeEvent(144,9,key,100,i));
				track.add(makeEvent(128,9,key,100,i+1));
			}
		}
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
		MidiEvent event=null;
		try{
			ShortMessage a=new ShortMessage();
			a.setMessage(comd,chan,one,two);
			event=new MidiEvent(a,tick);
		}catch(Exception e){
			e.printStackTrace();
		}
		return event;
	}

	@Override
	public void intialize() {
		// TODO Auto-generated method stub
		setUpMidi();
		buildTrackAndStart();
	}

	@Override
	public void on() {
		// TODO Auto-generated method stub
		sequencer.start();
		setBPM(90);
	}

	@Override
	public void off() {
		// TODO Auto-generated method stub
		setBPM(0);
		sequencer.stop();
	}

	@Override
	public void setBPM(int bmp) {
		// TODO Auto-generated method stub
		this.bmp=bmp;
		sequencer.setTempoInBPM(getBPM());
	}
	
	public int getBPM(){
		return bmp;
	}

	void beatEvent(){
		notifyBeatObervers();
	}
	@Override
	public void registerObserver(BeatObserver o) {
		// TODO Auto-generated method stub
		beatObservers.add(o);
	}
	
	public void notifyBeatObervers(){
		for(int i=0;i<beatObservers.size();i++){
			BeatObserver observer=(BeatObserver)beatObservers.get(i);
			observer.updateBeat();
		}
	}

	@Override
	public void removeObserver(BeatObserver o) {
		// TODO Auto-generated method stub
		int i=beatObservers.indexOf(o);
		if(i>=0){
			beatObservers.remove(i);
		}
	}

	@Override
	public void registerObserver(BPMObserver o) {
		// TODO Auto-generated method stub
		bpmObservers.add(o);
	}
	
	public void notifyBPMObservers(){
		for(int i=0; i<bpmObservers.size();i++){
			BPMObserver observer=(BPMObserver)bpmObservers.get(i);
			observer.updateBPM();
		}
	}

	@Override
	public void removeObserver(BPMObserver o) {
		// TODO Auto-generated method stub
		int i=bpmObservers.indexOf(o);
		if(i>=0){
			bpmObservers.remove(i);
		}
	}

}
