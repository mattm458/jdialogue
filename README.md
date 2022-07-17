# Brooklyn Speech Lab Dialogue Engine

This repository contains a modular spoken dialogue system capable of engaging a user in conversation. The system is designed so that many different audio recording, speech recognition, conversation model, feature transformation, and TTS modules can be swapped out to change its behavior.

The system was designed primarily for [entrainment research](https://academiccommons.columbia.edu/doi/10.7916/D8XP7DBD/download), so a key component of the system is swappable entrainment strategies. An entrainment strategy is a specialized component capable of directing the output of a TTS module, changing any available acoustic or prosodic parameters in response to those of its human conversation partner. As an example, a simple entrainment strategy included in the repository will make the TTS approximately match its partner's average vocal pitch and speaking rate over their turn in the conversation.

While the dialogue engine is under active development, it is currently limited in that it offers mostly simple modules. The following additional modules are under active research, in order from most to least activity:

* Neural entrainment strategies for more realistic entrainment behavior, learned from real-world conversation corpora like [Fisher](http://www.lrec-conf.org/proceedings/lrec2004/pdf/767.pdf), [Switchboard](https://www.computer.org/csdl/proceedings-article/icassp/1992/00225858/12OmNxGSmbC), the [Columbia Games Corpus](https://academiccommons.columbia.edu/doi/10.7916/D8BK1MMW/download), and our [Brooklyn Multi-Interaction Corpus](http://lrec-conf.org/proceedings/lrec2022/pdf/2022.lrec-1.183.pdf).
* TTS engines (specifically Tacotron) with controllable acoustic and prosodic parameters. We are interested in determining which parameters can be controlled reliably, as well as determining whether the model meets the targets we give it.
* Fast neural vocoders, such as [WaveRNN](https://proceedings.mlr.press/v80/kalchbrenner18a/kalchbrenner18a.pdf), and whether they can contribute to producing output with controllable acoustic and prosodic parameters.

The system was designed to be used in an experimental setting, and can handle multiple simultaneous conversations.