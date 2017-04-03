# you'll need to install these:

require 'diffy'
require "mini_magick"

# convert -size 800x600 xc:none empty.png

target = "test/util"

size = 4

yamls = Dir.glob(File.join(target,"yamls","*.txt"))


MiniMagick::Tool::Convert.new do |convert|
  convert.size "0x400"
  convert << "xc:none"
  convert.fill "magenta"
  convert << "#{target}/output.png"
end

yamls.each_cons(2) do |pair|
  MiniMagick::Tool::Convert.new do |convert|
    convert.size "#{size-1}x1000"
    convert << "xc:none"
    y = 0
    Diffy::Diff.new(pair[0], pair[1], :source => 'files').each do |line|
      char = line[0..1]
      convert.fill case char
        when "+ "
          "green"
        when "- "
          "red"
        when "  "
          "white"
        else
          "black"
        end
      convert.draw "rectangle 0,#{y} #{size-1},#{y+size-1}"
      y += size
    end
    convert << "#{target}/new_line.png"
  end

  MiniMagick::Tool::Convert.new do |convert|
    convert << "#{target}/output.png"
    convert << "#{target}/new_line.png"
    convert.append.+("#{target}/output.png")
  end
end
