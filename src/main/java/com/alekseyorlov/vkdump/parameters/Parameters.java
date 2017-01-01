package com.alekseyorlov.vkdump.parameters;

import org.kohsuke.args4j.Option;

import com.alekseyorlov.vkdump.parameters.annotation.Scope;
import com.alekseyorlov.vkdump.parameters.annotation.ValidScope;

@ValidScope
public class Parameters {

	@Scope(mask = 4)
	@Option(name = "-photos", usage = "Dump photos")
	private Boolean dumpPhotos;
	
	@Scope(mask = 8)
	@Option(name = "-audio", usage = "Dump audio")
	private Boolean dumpAudio;

	public Boolean getDumpPhotos() {
		return dumpPhotos;
	}

	public Boolean getDumpAudio() {
		return dumpAudio;
	}
	
}
