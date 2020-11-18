import {
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import videojs from 'video.js';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Component({
  selector: 'video-player-component',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.sass'],
  encapsulation: ViewEncapsulation.None
})
export class VideoPlayerComponent implements OnInit, OnDestroy {
  @ViewChild('target', {static: true}) target: ElementRef;
  @Input() playlist: string;
  @Input() imageCamera: string;
  @Input() imageMath: string;

  player: videojs.Player;

  channel: string = 'camera';

  _safeResourceUrl: SafeResourceUrl

  get safeResourceUrl(): SafeResourceUrl {
    const defaultImage = 'iVBORw0KGgoAAAANSUhEUgAAAkQAAAF6CAIAAAB3GtBNAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAA6oSURBVHhe7d1bYuI6EgDQWRcLYj1ZTTbDYmZsMKFkvQ3JhPQ5X/c2kqpUjkqdF/0fAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA/jGn88fH5+Xy34fL5fL5cT6dtgEA8Iudlltsu8CKLp9nNxoAv9j5c7uy2i4f7jMAfqXTR/Yp2eXzJvmK45X7DIDfJ73KLp/ZZXU6p5fd53l7AQB+h+QLjNV7KrnxfHIGwK8S77Lmp1zxOnObAfCLxCuq8+XDxtDBHx9Zte7B03n9tYD4Jc3t9wK2169CGvW1Srn+ZJKLJ8LF7MvWkFnEw5VJ7Td4i1Vb70VVbRut+VgNwph9BfqVT9cdCRcqVE+p9hsx2+tVg+U/Xnp4FyMn7a4+9vmO1vm9gPidvIEGEnvSV7/60SSfCjfSUq92E49W5ktrg5XfzHi+qm1TNR8pQVLcX3CZdTfYvNEGy3+o9PBWOictUR/7ZEcbaSGLe+fpdpC43qNd/WySz4QbDLVKpx6szM1A1P2UxZNVbZuteb8CuxUP1CBZtxduVT82oxsslP1usPzzpYd30zppe/Wxp9M5Ef6yuX6xJJEFyc7jpfR7AfFvqJ0WEjtE7HM/nOTxcHF/+6HJQou0AMcqs4ovLa5f5NqixReyvvpUVdvma94rwG6T7cvsHmwvfvGvHe6mfcROXz8qvJRrWXqze8rV6ywuniUc9lJLDv6M9klLjY8dOeObtL9csm8T3L5dslukuX5csdoEFt+d5M6xcOWh9S02p9anJRdHNjHOezr3UTHoutZYzVvh0xWvGpfZUPYj4weOzXp9bf8ZxGdS+0AOY7Ihs5uBtzZ+Qc2MnThGIye2oH6IY8tqLvj9SSYmwvWH1rd/rDJhUjHiePIT22w7VvN6+Lj7L/uFZ7MfGd+rbcNjam1m/Wm/8FHAO5g5aeNjx4/R4QNXO8VhwU4P/IEko4lFukPDgHyPByozkNtj1VdVte3gOrVpoSjRfi+zUUfGjx+bTFi+UvXaw14dLCG8p5mTNj52/Bi1DmNbeWaI3FvwJ5IMJlpLHFr8nll7pfnKPGZUU3tMb2c/sc2mgzUvhw+LLas1lp7NPo7Pn9TNY8h0RcLylSK0yvSqRwFvIRyG7gf8+NjxY9Q6jB2lqTPn90eSfJhILQxtWLpnZZnpyoQJA5o1mHkCLQdrXgofd7eu1Vh6NvuxJ/WluObtpz1661SK0CrTqx4FvIVwGLof8ONjx49R6zD25HOnju/PJPllIrcwtGL9NKCxxmxlwvgBzRpMPYKGgzXPw8e93VZqLD2bff9JJfI1z8kPLTZUitAq06seBbyFcBi6H/DjY8ePUeswdoUw18lzp/eHkrybSC4Mbar/Qu1kZcIGBzRrMLHNpoM134ePO7uv01h6NvswfsR+zYm6V4rQKtOrHgW8hXgaO03j2NDOMTrYszYhzjJ78vD+VJKbiex6Q8PrtYTCkIHKPDY4UrimiW02Hax5Gj4sElZpLD2b/cj4EC4dEyZX/l4SRlSK0CrT7GbgvZXPe0Fy9DpnY/wYPXng4mn/mFzqx5K8mVikPzTuu/zQ4ohuZV6ywZtXLXVwnTgtfh8qVum3XGaPF2pzw/LlxxwG5CNmNwNvLt5mtc6Ynpr+0Zg4Rv3L9PTxWf1yWprX3djR/bkkrybCDQwNGVWe2VRlwnJjxaua2GbbsZqP7LpRu9nsR8ZXa/t4oTY3LF8sQdxtvsbsZuDdxROxfNi/4h/nnDlGsWkto9Pu9AhdC5pmfzV4cn8wydVEuP7QmM9LKrPbYDbsdP68XPoPfmqbHYdqPrLpsPB+R7PZj4wP4dIxjTw2YfnCiGSvhfCzm4H3l5yKm9K74N2MnIupY5Q0rdX9Teb2sSsnfj9/9OD+aJJT4eLQwm8vfTTenDExU5nsY6D4RojL9jqVmqpq25Ga59vIkwjr7p/WbPYj40O43Zi4wfWbZvHF/dszpplmb7Vfiv7CRwHvI2scZYOHYvIYZR2oqHpNpLkPn9ufTXIm3Fiom+ZaU5UZi7p8irSNL5usatt8zXcziimEqvx/L7PRU7dKM91PLH7YvfRRwBvp/MNKlR+4Kpo/Rp3g7VXi0R4/tj+c5ES4MLSjt9JkZdYvJm6jS0Y+Al7dQSdrnpSukkAoyv/5Muts7xJ+hqVxmdUiv/pRwFs51f7J26nDcPAYXb+yEoMPxj4W7vcmGYbW3GJu4+uO7LG2we3ljoNVbRuv+Uj433SZrba3AAnbuz/esHzpMus8l/Wi3Oa/7lEA3+db+uefoDIA70LHrlEZgHehY9eoDMBvd/vn+j/it8917CuVAXgbyc9zXRV/PPkfpDIAbyNr2Tr2RmUA3kbSsi/Vf6fyH6QyAG/j9HF7a6OP8+hvQP0rVAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA7k7nj8/L5fLfh+X/Pj/Op+31MefPbXLf5aO+9D6ZNZXzqTH+IybeVQ49mHsr72IV1yJWp/Qzv+19G54Ic6tJhTGf5+3Pdm45b4NWnZS/PLPZLeHrEtsf3adfh9YtU7J85z9QgT/oFBtKbukUw43i+cuslUytq8cuOeJbLrPTuVXFWhGHMy+E7V9myeqly6yV81LtbVTu6c2uCVfWqMftfqC60eDfNdhNG40tevIyG0immMjgJu7Krf+py2xk8sEd32XTd3dDZrd2Vrl+7PJeX7HZy2fzNjxcqcGPU+CPydrSZekyV/ELOeN/5T2dzonwV+n1S0GJbMldu7p+8Wi1+6pSoV/FmfcN7IU1yj061OKrCl/ak/MybrOSDZXmJv19V6CkfIv97Dg3Typr/ru6pa8vwa/Rd7X+ts3ePB7x/nOu0qV0Ot/nX7M9bXZVcp3Bv2fXz7LvO9y+KVLoZ8Pa/TYVm2Q2OKba7JLVMPGuKo4JA7KG2AyQlnF/8e9f3f540808Tq9fR9ncNOpVOjtWe7du46V8Owc3u2jnnAXerNfX9p9BM2Xgr/v+FhD6U7lXP4RkikNbS42E6a0fB7RaeD55ZuX9gH7m9bTqc8MrD8ns1mabL75os+XJYW4hrabH1EodgT+r30ifNh5iYOSjX+0b3UiYmS6cNdJWgKnM90t3J4cBo7sO0aLx2+GxwLdttpJN6xm0NeoE/HHHO8ewgd63aTTQu8di+yEjYcJuy2Na5WgFGChjfXp8pfg9s8bGyquGdJZ8BpK7uX776ao55WWbzXZz1R9RE2a2Nwr8OcON7rjx5hR7cN8u35EwIUB5TKscrQCv6u8Nyz1XyLi0aizjms1Acqs4bVP+qZ9Xbbb8CHojbj/t0atYa6PAHzTY6J7Rb193hXbasMt3JEwIUB7TKkcrwKv6e8X6CVt5S/mqsYi3XAaSW8WJm0vxNnvVZsuPoDmi+attUWujwB802Oie0W9fd4V22rDLdyRMCFAe0ypHu8lurzzZ35tKN8t+1VjCeyYDya3i1OjbNpstfFMfUcuwoLVR4A8abHTP6Levu0cynYElI2HCbstjWuVoBRgoY316L/PwerZ6OjekEUYOJJdJltpl9a2brY+IZSh//TOMGN4o8Df0e8vTxkM8lczI5NCGi2PCGnk7bAUYCF6/AvqTw4jG3PiNpDjsyGW2qE571WYr2dTmPv68FrVeJuDPC62j0gFOH5/FvwePGuh9dyGZ3tDMSJjO+rHR5q83A3QzbwwYyDzMTh9STPnLbpXa5E7casxXbbY8OcxN4z5eKAdN1i5/KAN/WGweS5tIr63Huwcd7w4DvfrLLpls9PrmtpdyKiNhGk02WaAcuxUgmbwUK3n9UcWrff79zKsNfhf3KlsjzE4n15fdLbx78UWbXWS5NsJWt/ElTK6MAP6w2NKu7u9KuP/554MNIrSYSq8O0l63+HqLxCSbgR+FKAlbTcZkb8VeWqETIC9jIe9FPjcunP+e2e5tB3fT83rlqdVvgXT29d0Ot4jJqvmDf8lmN/ew3fdmjEHXb5rFpfdvz+gyg39R3mBKjvaH2KsLl8DeWDLLp5Db+LuRMLXLbN+bi1vtBsg6fEFp5tiOb/bzd3OLiYW8so0NxP62zab/DtpeYfJIzI3LDP5RvX8mqtjQxnQvgUz7X8q6/q18GxmMhAndMBmTdMna7IEA7cTLv/acLNyRx03mVtJqXWaLds6VnS6e3uyydm3rlS8lL7MbMZfL8etFlxn8y27vrRC6xfI/66/rVvvZkIFLoKSWzPZyZiRM+zJrr39tpFuI5j6yxPsLx8FF1RUmd13r8bect0Gr0Qf/zGZvCe9CL9M7cbe3ACnEDGu7zAD4PiO3LwD8ai4zAN6eywyAt+cyA+DtucwAeHsuMwDenssMgLe3/pLYpvXraAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMCP+M9//gcZaeU06fs0hAAAAABJRU5ErkJggg==';
    const encodedImage = this.getEncodedFrame();
    return this._sanitizer.bypassSecurityTrustResourceUrl(
      'data:image/jpeg;base64, ' + (encodedImage ? encodedImage : defaultImage)
    );
  }

  constructor(
    private elementRef: ElementRef,
    private _sanitizer: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this.player = videojs(this.target.nativeElement, { autoplay: false, controls: true, preload: 'auto', liveui: true, html5: { vhs: { overrideNative: true }, nativeAudioTracks: false, nativeVideoTracks: false}, sources: [{ src: this.playlist, type: 'application/x-mpegURL', }]}, function onPlayerReady() {
      console.log('onPlayerReady', this);
    });
  }

  ngOnDestroy() {
    if (this.player) {
      this.player.dispose();
    }
  }

  setSource = (evt) => {
    this.channel = evt.target.value;

    const player = document.querySelector('.video-js');
    const imageContainer = document.querySelector('.image-container');

    if (this.channel === 'video') {
      player.classList.remove('hidden')
      imageContainer.classList.add('hidden')
    } else {
      player.classList.add('hidden')
      imageContainer.classList.remove('hidden')
    }
  }

  getEncodedFrame = () => {
    switch (this.channel) {
      case 'camera':
        return this.imageCamera;
      case 'math':
        return this.imageMath;
      default:
        return null;
    }
  }
}
